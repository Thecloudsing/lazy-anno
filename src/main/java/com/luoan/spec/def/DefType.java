package com.luoan.spec.def;

import com.luoan.core.Application;
import com.luoan.spec.AbstractSpec;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class DefType extends AbstractSpec {
    public static final JCTree.JCExpression Void = initType(new Type.JCVoidType());

    private static JCTree.JCExpression initType(Type type) {
        return Application.application.getTreeMaker().Type(type);
    }
}
