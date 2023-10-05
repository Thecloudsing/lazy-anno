package com.luoan.spec;

import com.luoan.spec.def.DefModifiers;
import com.luoan.spec.def.DefType;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class MethodSpec extends AbstractSpec {

    private final JCTree.JCModifiers modifiers;
    private final Name name;
    private final String methodName;
    private final JCTree.JCExpression returnType;
    private final List<JCTree.JCTypeParameter> typeParameterList;
    private final List<JCTree.JCVariableDecl> parameterList;
    private final List<JCTree.JCExpression> thrownList;
    private final JCTree.JCBlock block;
    private final JCTree.JCExpression defaultValue;

    private MethodSpec(Builder builder) {
        this.name = builder.name;
        this.modifiers = builder.modifiers;
        this.methodName = builder.methodName;
        this.returnType = builder.returnType;
        this.typeParameterList = builder.typeParameterList;
        this.parameterList = builder.parameterList;
        this.thrownList = builder.thrownList;
        this.block = builder.block;
        this.defaultValue = builder.defaultValue;
    }

    public static Builder builder(Name name) {
        return new Builder(name);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    private JCTree.JCMethodDecl create() {
        return createMethod(
                modifiers,
                name,
                returnType,
                typeParameterList,
                parameterList,
                thrownList,
                block,
                defaultValue
        );
    }

    /**
     * 创建方法
     *
     * @param modifiers         修饰 符
     * @param methodName        方法名称
     * @param returnType        返回类型
     * @param typeParameterList 类型参数列表
     * @param parameterList     参数列表
     * @param thrownList        异常列表
     * @param block             块
     * @param defaultValue      def null
     * @return {@link JCTree.JCMethodDecl}
     */
    private JCTree.JCMethodDecl createMethod(JCTree.JCModifiers modifiers,
                                             Name methodName,
                                             JCTree.JCExpression returnType,
                                             List<JCTree.JCTypeParameter> typeParameterList,
                                             List<JCTree.JCVariableDecl> parameterList,
                                             List<JCTree.JCExpression> thrownList,
                                             JCTree.JCBlock block,
                                             JCTree.JCExpression defaultValue) {
        return treeMaker.MethodDef(
                modifiers,
                methodName,
                returnType,
                typeParameterList,
                parameterList,
                thrownList,
                block,
                defaultValue
        );
    }

    @Override
    public String toString() {
        return "MethodSpec{" +
                "modifiers=" + modifiers +
                ", methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                ", typeParameterList=" + typeParameterList +
                ", parameterList=" + parameterList +
                ", thrownList=" + thrownList +
                ", block=" + block +
                ", defaultValue=" + defaultValue +
                '}';
    }

    public static class Builder {
        private final String methodName;
        private final Name name;
        private JCTree.JCModifiers modifiers = DefModifiers.Public;
        private JCTree.JCExpression returnType = DefType.Void;
        private List<JCTree.JCTypeParameter> typeParameterList = List.nil();
        private List<JCTree.JCVariableDecl> parameterList = List.nil();
        private List<JCTree.JCExpression> thrownList = List.nil();
        private JCTree.JCBlock block = null;
        private JCTree.JCExpression defaultValue = null;

        private Builder(Name name) {
            this.name = name;
            this.methodName = name.toString();
        }

        private Builder(String name) {
            this.methodName = name;
            this.name = AbstractSpec.initName(name);
        }

        public Builder addModifiers(JCTree.JCModifiers modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public Builder addReturnType(JCTree.JCExpression returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder addTypeParameterList(List<JCTree.JCTypeParameter> typeParameterList) {
            this.typeParameterList = typeParameterList;
            return this;
        }

        public Builder addParameterList(List<JCTree.JCVariableDecl> parameterList) {
            this.parameterList = parameterList;
            return this;
        }

        public Builder addThrownList(List<JCTree.JCExpression> thrownList) {
            this.thrownList = thrownList;
            return this;
        }

        public Builder addBlock(JCTree.JCBlock block) {
            this.block = block;
            return this;
        }

        public Builder addDefaultValue(JCTree.JCExpression defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public JCTree.JCMethodDecl build() {
            return new MethodSpec(this).create();
        }
    }
}
