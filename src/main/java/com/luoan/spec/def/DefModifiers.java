package com.luoan.spec.def;

import com.luoan.core.Application;
import com.luoan.spec.AbstractSpec;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class DefModifiers extends AbstractSpec {

    public static final JCTree.JCModifiers Default = initModifiers(0, List.nil());
    public static final JCTree.JCModifiers Public = initModifiers(Flags.PUBLIC, List.nil());
    public static final JCTree.JCModifiers Private = initModifiers(Flags.PRIVATE, List.nil());
    public static final JCTree.JCModifiers Protected = initModifiers(Flags.PROTECTED, List.nil());

    private static JCTree.JCModifiers initModifiers(long flags, List<JCTree.JCAnnotation> annotations) {
        return Application.application.getTreeMaker().Modifiers(flags, annotations);
    }
}
