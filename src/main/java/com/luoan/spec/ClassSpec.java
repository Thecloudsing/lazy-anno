package com.luoan.spec;

import com.luoan.spec.def.DefModifiers;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class ClassSpec extends AbstractSpec {
    private final JCTree.JCModifiers modifiers;
    private final Name name;
    private final String className;
    private final List<JCTree.JCTypeParameter> typeParameterList;
    private final JCTree.JCExpression parentClass;
    private final List<JCTree.JCExpression> interfaceList;
    private final List<JCTree> members;

    private ClassSpec(Builder builder) {
        this.name = builder.name;
        this.modifiers = builder.modifiers;
        this.className = builder.className;
        this.typeParameterList = builder.typeParameterList;
        this.parentClass = builder.parentClass;
        this.interfaceList = builder.interfaceList;
        this.members = builder.members;
    }

    public static Builder builder(Name name) {
        return new Builder(name);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    private JCTree.JCClassDecl create() {
        return createClass(
                modifiers,
                name,
                typeParameterList,
                parentClass,
                interfaceList,
                members
        );
    }

    /**
     * 创建类
     *
     * @param modifiers         修饰 符
     * @param className         类名
     * @param typeParameterList 类型参数列表
     * @param parentClass       父类
     * @param interfaceList     接口列表
     * @param members           成员
     * @return {@link JCTree.JCClassDecl}
     */
    private JCTree.JCClassDecl createClass(JCTree.JCModifiers modifiers,
                                           Name className,
                                           List<JCTree.JCTypeParameter> typeParameterList,
                                           JCTree.JCExpression parentClass,
                                           List<JCTree.JCExpression> interfaceList,
                                           List<JCTree> members) {
        return treeMaker.ClassDef(
                modifiers,
                className,
                typeParameterList,
                parentClass,
                interfaceList,
                members
        );
    }

    @Override
    public String toString() {
        return "ClassSpec{" +
                "modifiers=" + modifiers +
                ", className='" + className + '\'' +
                ", typeParameterList=" + typeParameterList +
                ", parentClass=" + parentClass +
                ", interfaceList=" + interfaceList +
                ", members=" + members +
                '}';
    }

    public static class Builder {
        private final Name name;
        private final String className;
        private JCTree.JCModifiers modifiers = DefModifiers.Default;
        private List<JCTree.JCTypeParameter> typeParameterList = List.nil();
        private JCTree.JCExpression parentClass = null;
        private List<JCTree.JCExpression> interfaceList = List.nil();
        private List<JCTree> members = List.nil();

        private Builder(String name) {
            this.className = name;
            this.name = AbstractSpec.initName(className);
        }

        private Builder(Name name) {
            this.name = name;
            this.className = name.toString();
        }

        public Builder addModifiers(JCTree.JCModifiers modifiers) {
            this.modifiers = modifiers;
            return this;
        }

        public Builder addTypeParameterList(List<JCTree.JCTypeParameter> typeParameterList) {
            this.typeParameterList = typeParameterList;
            return this;
        }

        public Builder addParentClass(JCTree.JCExpression parentClass) {
            this.parentClass = parentClass;
            return this;
        }

        public Builder addInterfaceList(List<JCTree.JCExpression> interfaceList) {
            this.interfaceList = interfaceList;
            return this;
        }

        public Builder addMembers(List<JCTree> members) {
            this.members = members;
            return this;
        }

        public JCTree.JCClassDecl build() {
            return new ClassSpec(this).create();
        }
    }
}
