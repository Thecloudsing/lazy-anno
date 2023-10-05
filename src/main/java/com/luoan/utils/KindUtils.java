package com.luoan.utils;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.tree.JCTree;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class KindUtils {
    public static boolean eqVariable(JCTree jcTree) {
        return Tree.Kind.VARIABLE.equals(jcTree.getKind());
    }

    public static boolean eqMethod(JCTree jcTree) {
        return Tree.Kind.METHOD.equals(jcTree.getKind());
    }
}
