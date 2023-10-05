package com.luoan.handler;

import com.luoan.code.AbstractClassCode;
import com.luoan.core.Application;
import com.luoan.core.Process;
import com.luoan.handler.factory.AnnotationHandlerFactory;
import com.luoan.scan.NodeRootRecord;
import com.luoan.utils.KindUtils;
import com.luoan.utils.LambdaTools;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/1
 */
public abstract class AbstractHandler extends AbstractClassCode implements Process {
    protected Filer filer;
    protected boolean isInit = Boolean.TRUE;
    protected JCTree.JCClassDecl jcClassDecl;
    protected NodeRootRecord nodeRootRecord;
    protected Set<Integer> deprecatedMembersIndexSet;
    protected ListBuffer<JCTree.JCMethodDecl> newMethodList;
    protected ListBuffer<JCTree.JCVariableDecl> newGlobalVarList;

    protected AbstractHandler() {
        init();
    }

    private void init() {
        AnnotationHandlerFactory.registerHandler(this.getAnnotation(), this);
        Application application = Application.application;
        filer = application.getFiler();
    }

    public void exec(NodeRootRecord nodeRootRecord) {
        this.nodeRootRecord = nodeRootRecord;
        this.newMethodList = new ListBuffer<>();
        this.newGlobalVarList = new ListBuffer<>();
        this.deprecatedMembersIndexSet = new HashSet<>();
    }

    @Override
    public void process() {
        Element element = nodeRootRecord.getElement();
        if (isInit) {
            isInit = Boolean.FALSE;
            doInit();
        }
        JCTree tree = javacTrees.getTree(element);
        if (!(tree instanceof JCTree.JCClassDecl)) return;
        jcClassDecl = (JCTree.JCClassDecl) tree;
        nodeRootRecord.getNodeAnnotationRecords().forEach(annotationRecord -> {
            ElementType elementType = annotationRecord.getElementType();
            NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord
                    = annotationRecord.getNodeIndexRecord();

            if (elementType.equals(ElementType.TYPE))
                classDef(jcClassDecl);
            else if (elementType.equals(ElementType.METHOD))
                methodDef(jcClassDecl, nodeIndexRecord.getMembersIndex());
            else if (elementType.equals(ElementType.FIELD))
                globalVar(jcClassDecl, nodeIndexRecord.getMembersIndex());
            else if (elementType.equals(ElementType.PARAMETER))
                paramVar((JCTree.JCMethodDecl) jcClassDecl.getMembers()
                                .get(nodeIndexRecord.getMembersIndex()),
                        nodeIndexRecord.getMembersIndex(),
                        nodeIndexRecord.getInnerIndex());
            else if (elementType.equals(ElementType.LOCAL_VARIABLE))
                localVar((JCTree.JCMethodDecl) jcClassDecl.getMembers()
                                .get(nodeIndexRecord.getMembersIndex()),
                        nodeIndexRecord.getMembersIndex(),
                        nodeIndexRecord.getInnerIndex());
        });
        commit();
    }

    protected abstract void doInit();

    protected abstract List<JCTree.JCImport> initImport();

    protected abstract void classDef(JCTree.JCClassDecl jcClassDecl);

    protected abstract void methodDef(JCTree.JCClassDecl jcClassDecl, int membersIndex);

    protected abstract void globalVar(JCTree.JCClassDecl jcClassDecl, int membersIndex);

    protected abstract void paramVar(JCTree.JCMethodDecl jcMethodDecl, int membersIndex, int innerIndex);

    protected abstract void localVar(JCTree.JCMethodDecl jcMethodDecl, int membersIndex, int innerIndex);

    protected void commit() {
        List<JCTree.JCImport> jcImports = initImport();
        super.addImportInClass(nodeRootRecord.getElement(), jcImports);
        ListBuffer<JCTree> jcTrees = new ListBuffer<>();
        jcClassDecl.getMembers().stream()
                .filter(LambdaTools.filterWithIndex((item, index) -> !deprecatedMembersIndexSet.contains(index)))
                .forEach(jcTrees::add);
        jcTrees.addAll(newGlobalVarList);
        jcTrees.addAll(newMethodList);
        jcClassDecl.defs = jcTrees.toList();
    }

    protected abstract String getAnnotation();

    protected ListBuffer<JCTree.JCIdent> getCurrentGlobalVar() {
        ListBuffer<JCTree.JCIdent> jcIdents = new ListBuffer<>();
        jcClassDecl.getMembers().stream()
                .filter(KindUtils::eqVariable)
                .map(jcTree -> treeMaker.Ident(((JCTree.JCVariableDecl) jcTree).getName()))
                .forEach(jcIdents::add);
        return jcIdents;
    }
}
