package com.luoan.spec;

import com.luoan.spec.def.DefModifiers;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class VarSpec extends AbstractSpec {

    private final JCTree.JCModifiers modifiers;
    private final Name name;
    private final String varName;
    private final JCTree.JCExpression classIdent;
    private final JCTree.JCExpression initVar;

    private VarSpec(Builder builder) {
        this.name = builder.name;
        this.modifiers = builder.modifiers;
        this.varName = builder.varName;
        this.classIdent = builder.classIdent;
        this.initVar = builder.initVar;
    }

    public static Builder builder(Name name, JCTree.JCExpression classIdent) {
        return new Builder(name, classIdent);
    }

    public static Builder builder(String name, JCTree.JCExpression classIdent) {
        return new Builder(name, classIdent);
    }

    private JCTree.JCVariableDecl create() {
        return createVar(
                modifiers,
                name,
                classIdent,
                initVar
        );
    }

    /**
     * 创建变量
     *
     * @param modifiers  修饰 符
     * @param varName    变量名称
     * @param classIdent 类标识
     * @param initVar    初始化
     * @return {@link JCTree.JCVariableDecl}
     */
    private JCTree.JCVariableDecl createVar(JCTree.JCModifiers modifiers,
                                            Name varName,
                                            JCTree.JCExpression classIdent,
                                            JCTree.JCExpression initVar) {
        return treeMaker.VarDef(
                modifiers,
                varName,
                classIdent,
                initVar
        );
    }

    @Override
    public String toString() {
        return "VarSpec{" +
                "modifiers=" + modifiers +
                ", varName='" + varName + '\'' +
                ", classIdent=" + classIdent +
                ", initVar=" + initVar +
                '}';
    }

    public static class Builder {
        private final Name name;
        private final String varName;
        private final JCTree.JCExpression classIdent;
        private JCTree.JCModifiers modifiers = DefModifiers.Default;
        private JCTree.JCExpression initVar = null;

        private Builder(Name name, JCTree.JCExpression classIdent) {
            this.name = name;
            this.classIdent = classIdent;
            this.varName = name.toString();
        }

        private Builder(String name, JCTree.JCExpression classIdent) {
            this.varName = name;
            this.classIdent = classIdent;
            this.name = AbstractSpec.initName(name);
        }

        public Builder addModifiers(JCTree.JCModifiers modifiers) {
            this.modifiers = modifiers;
            return this;
        }


        public Builder addInitVar(JCTree.JCExpression initVar) {
            this.initVar = initVar;
            return this;
        }

        public JCTree.JCVariableDecl build() {
            return new VarSpec(this).create();
        }
    }
}
