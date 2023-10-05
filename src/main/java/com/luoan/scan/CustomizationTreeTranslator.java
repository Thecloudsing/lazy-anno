package com.luoan.scan;

import com.luoan.utils.KindUtils;
import com.luoan.utils.NodeRecordUtils;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import javafx.util.Pair;

import javax.lang.model.element.Element;
import java.lang.annotation.ElementType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class CustomizationTreeTranslator extends TreeTranslator {
    private final Element element;
    private final AbstractScan abstractScan;
    private final Map<String, Set<NodeRootRecord.NodeAnnotationRecord>> annotationNodeSetRecordMap = new HashMap<>();
    private int localIndex = -1;
    private int membersIndex = -1;
    private int methodParamIndex = -1;

    public CustomizationTreeTranslator(Element element, AbstractScan abstractScan) {
        this.element = element;
        this.abstractScan = abstractScan;
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        processClass(jcClassDecl);
        abstractScan.commit(createNodeRootRecords());
        super.visitClassDef(jcClassDecl);
    }

    private Map<String, NodeRootRecord> createNodeRootRecords() {
        HashMap<String, NodeRootRecord> annotationNodeRootRecordMap = new HashMap<>();
        annotationNodeSetRecordMap.forEach((annotationCanonicalName, nodeAnnotationRecordSet) -> {
            NodeRootRecord nodeRootRecord = NodeRootRecord.Builder(element, nodeAnnotationRecordSet).build();
            annotationNodeRootRecordMap.put(annotationCanonicalName, nodeRootRecord);
        });
        return annotationNodeRootRecordMap;
    }

    /**
     * 处理变量
     *
     * @param jcTree JC 树
     * @return {@link JCTree.JCVariableDecl}
     */
    private JCTree.JCVariableDecl processVar(JCTree jcTree) {
        return (JCTree.JCVariableDecl) jcTree;
    }

    /**
     * 扫描局部变量
     *
     * @param jcTree JC 树
     */
    private void processLocalVar(JCTree jcTree) {
        JCTree.JCModifiers modifiers = processVar(jcTree).getModifiers();
        Pair<Boolean, List<String>> pair = abstractScan.annotationScan(modifiers);
        localIndex++;
        if (pair.getKey()) return;
        //创建节点下标记录
        NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord =
                NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord.builder(membersIndex).addInnerIndex(localIndex).build();
        //创建节点注解记录 ==>> 因为一个类中可能有多个同一个注解所以以集合形式保存
        NodeRootRecord.NodeAnnotationRecord annotationRecord =
                NodeRootRecord.NodeAnnotationRecord.builder(ElementType.LOCAL_VARIABLE, modifiers, nodeIndexRecord).build();

        pair.getValue().forEach(annotationCanonicalName -> {
            NodeRecordUtils.addNodeRecord(annotationCanonicalName, annotationRecord, annotationNodeSetRecordMap);
        });
    }

    /**
     * 扫描参数变量
     *
     * @param jcTree JC 树
     */
    private void processParamVar(JCTree jcTree) {
        JCTree.JCModifiers modifiers = processVar(jcTree).getModifiers();
        Pair<Boolean, List<String>> pair = abstractScan.annotationScan(modifiers);
        methodParamIndex++;
        if (pair.getKey()) return;
        //创建节点下标记录
        NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord =
                NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord.builder(membersIndex).addInnerIndex(methodParamIndex).build();
        //创建节点注解记录 ==>> 因为一个类中可能有多个同一个注解所以以集合形式保存
        NodeRootRecord.NodeAnnotationRecord annotationRecord =
                NodeRootRecord.NodeAnnotationRecord.builder(ElementType.PARAMETER, modifiers, nodeIndexRecord).build();

        pair.getValue().forEach(annotationCanonicalName -> {
            NodeRecordUtils.addNodeRecord(annotationCanonicalName, annotationRecord, annotationNodeSetRecordMap);
        });
    }

    /**
     * 扫描全局变量
     *
     * @param jcTree JC 树
     */
    private void processGlobalVar(JCTree jcTree) {
        JCTree.JCModifiers modifiers = processVar(jcTree).getModifiers();
        Pair<Boolean, List<String>> pair = abstractScan.annotationScan(modifiers);
        membersIndex++;
        if (pair.getKey()) return;
        //创建节点下标记录
        NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord =
                NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord.builder(membersIndex).build();
        //创建节点注解记录 ==>> 因为一个类中可能有多个同一个注解所以以集合形式保存
        NodeRootRecord.NodeAnnotationRecord annotationRecord =
                NodeRootRecord.NodeAnnotationRecord.builder(ElementType.FIELD, modifiers, nodeIndexRecord).build();

        pair.getValue().forEach(annotationCanonicalName -> {
            NodeRecordUtils.addNodeRecord(annotationCanonicalName, annotationRecord, annotationNodeSetRecordMap);
        });
    }

    /**
     * 扫描方法
     * 参数变量
     * 局部变量
     *
     * @param jcTree JC 树
     */
    private void processMethod(JCTree jcTree) {
        JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) jcTree;
        JCTree.JCModifiers modifiers = jcMethodDecl.getModifiers();
        Pair<Boolean, List<String>> pair = abstractScan.annotationScan(modifiers);
        membersIndex++;
        if (!pair.getKey()) {
            //创建节点下标记录
            NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord =
                    NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord.builder(membersIndex).build();
            //创建节点注解记录 ==>> 因为一个类中可能有多个同一个注解所以以集合形式保存
            NodeRootRecord.NodeAnnotationRecord annotationRecord =
                    NodeRootRecord.NodeAnnotationRecord.builder(ElementType.METHOD, modifiers, nodeIndexRecord).build();

            //扫描方法注解
            pair.getValue().forEach(annotationCanonicalName -> {
                NodeRecordUtils.addNodeRecord(annotationCanonicalName, annotationRecord, annotationNodeSetRecordMap);
            });
        }
        //扫描方法参数注解
        methodParamIndex = -1;
        jcMethodDecl.getParameters().forEach(this::processParamVar);
        //扫描方法局部变量注解]
        localIndex = -1;
        jcMethodDecl.getBody().getStatements().stream()
                .filter(KindUtils::eqVariable).forEach(this::processLocalVar);
    }

    /**
     * 扫描类及成员
     * class --->
     * |
     * ---> globalVar
     * |
     * ---> method --->
     * |
     * ---> paramVar
     * |
     * ---> localVar
     *
     * @param jcTree JC 树
     */
    private void processClass(JCTree jcTree) {
        JCTree.JCClassDecl jcClassDecl = (JCTree.JCClassDecl) jcTree;
        JCTree.JCModifiers modifiers = jcClassDecl.getModifiers();
        Pair<Boolean, List<String>> pair = abstractScan.annotationScan(modifiers);
        if (!pair.getKey()) {
            //创建节点下标记录
            NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord nodeIndexRecord =
                    NodeRootRecord.NodeAnnotationRecord.NodeIndexRecord.builder(membersIndex).addInnerIndex(-1).build();
            //创建节点注解记录 ==>> 因为一个类中可能有多个同一个注解所以以集合形式保存
            NodeRootRecord.NodeAnnotationRecord annotationRecord =
                    NodeRootRecord.NodeAnnotationRecord.builder(ElementType.TYPE, modifiers, nodeIndexRecord).build();

            pair.getValue().forEach(annotationCanonicalName -> {
                NodeRecordUtils.addNodeRecord(annotationCanonicalName, annotationRecord, annotationNodeSetRecordMap);
            });
        }
        //扫描类成员注解
        jcClassDecl.getMembers().forEach(action -> {
            if (KindUtils.eqVariable(action))
                processGlobalVar(action);
            else if (KindUtils.eqMethod(action))
                processMethod(action);
        });
    }
}
