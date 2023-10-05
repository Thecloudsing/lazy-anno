package com.luoan.code;

import com.luoan.core.Application;
import com.luoan.core.Process;
import com.luoan.utils.StringUtils;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePath;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.Element;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/3
 */
public abstract class AbstractClassCode implements Process {
    protected JavacTrees javacTrees;
    protected TreeMaker treeMaker;
    protected Names names;
    protected boolean init = Boolean.TRUE;

    protected AbstractClassCode() {
        Application application = Application.application;
        javacTrees = application.getJavacTrees();
        treeMaker = application.getTreeMaker();
        names = application.getNames();
    }

    /**
     * 在类中添加导入
     *
     * @param element        元素
     * @param trackerPackage 跟踪器包
     * @param className      类名
     */
    protected void addImportInClass(Element element, String trackerPackage, String className) {
        TreePath treePath = javacTrees.getPath(element);
        Tree leaf = treePath.getLeaf();
        if (treePath.getCompilationUnit() instanceof JCTree.JCCompilationUnit
                && leaf instanceof JCTree) {
            JCTree.JCCompilationUnit jcCU = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();
            if (jcCU.getImports().stream()
                    .filter(jcImport -> jcImport.qualid instanceof JCTree.JCFieldAccess)
                    .map(jcImport -> (JCTree.JCFieldAccess) jcImport.qualid)
                    .anyMatch(jcFieldAccess -> StringUtils.eq(jcFieldAccess.selected.toString(), trackerPackage)
                            && StringUtils.eq(jcFieldAccess.name.toString(), className))) return;
            ListBuffer<JCTree> jcTrees = new ListBuffer<>();
            jcTrees.addAll(jcCU.defs);
            JCTree.JCIdent trackerPackageIdent = treeMaker.Ident(names.fromString(trackerPackage));
            JCTree.JCImport anImport = treeMaker.Import
                    (treeMaker.Select(trackerPackageIdent, names.fromString(className)), false);
            if (!jcTrees.contains(anImport)) jcTrees.add(anImport);
            jcCU.defs = List.from(jcTrees);
        }
    }

    /**
     * 在类中添加导入
     *
     * @param element   元素
     * @param jcImports 导入列表
     */
    protected void addImportInClass(Element element, List<JCTree.JCImport> jcImports) {
        TreePath treePath = javacTrees.getPath(element);
        if (treePath == null) return;
        Tree leaf = treePath.getLeaf();
        if (treePath.getCompilationUnit() instanceof JCTree.JCCompilationUnit
                && leaf instanceof JCTree) {
            JCTree.JCCompilationUnit jcCU = (JCTree.JCCompilationUnit) treePath.getCompilationUnit();

            ListBuffer<JCTree> jcImportList = new ListBuffer<>();
            jcImports.stream()
                    .filter(jcImport -> jcCU.defs.stream().allMatch(predicate -> StringUtils.ne(jcImport, predicate)))
                    .forEach(jcImportList::add);
            jcImportList.addAll(jcCU.defs);
            jcCU.defs = List.from(jcImportList);

        }
    }

    protected JCTree.JCImport makerImport(String trackerPackage, Name className) {
        return treeMaker.Import(treeMaker.Select(treeMaker.Ident(names.fromString(trackerPackage)), className), false);
    }

    /**
     * 新类
     *
     * @param encl              def null
     * @param typeParameterList 类型参数列表
     * @param classIdent        类标识
     * @param parameterList     参数列表
     * @param anonymityClass    匿名类
     * @return {@link JCTree.JCNewClass}
     */
    protected JCTree.JCNewClass newClass(JCTree.JCExpression encl,
                                         List<JCTree.JCExpression> typeParameterList,
                                         JCTree.JCExpression classIdent,
                                         List<JCTree.JCExpression> parameterList,
                                         JCTree.JCClassDecl anonymityClass) {
        return treeMaker.NewClass(
                encl,
                typeParameterList,
                classIdent,
                parameterList,
                anonymityClass
        );
    }

    /**
     * 调用方法
     *
     * @param expression 表达式
     * @param methodName 方法名称
     * @param paramList  参数
     * @return {@link JCTree.JCExpressionStatement}
     */
    protected JCTree.JCExpressionStatement invokeMethod(
            JCTree.JCExpression expression, String methodName, List<JCTree.JCExpression> paramList) {
        return treeMaker.Exec(treeMaker.Apply(
                List.nil(),
                treeMaker.Select(expression, names.fromString(methodName)),
                paramList
        ));
    }

    protected JCTree.JCExpressionStatement invokeMethod(
            JCTree.JCExpression var,
            JCTree.JCExpression expression,
            String methodName,
            List<JCTree.JCExpression> paramList) {
        return assignVar(var, treeMaker.Apply(
                List.nil(),
                treeMaker.Select(expression, names.fromString(methodName)),
                paramList
        ));
    }

    /**
     * 赋值变量
     *
     * @param var        变量
     * @param expression 表达
     * @return {@link JCTree.JCStatement}
     */
    protected JCTree.JCExpressionStatement assignVar(JCTree.JCExpression var, JCTree.JCExpression expression) {
        return treeMaker.Exec(treeMaker.Assign(var, expression));
    }

}
