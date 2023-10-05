package com.luoan.scan;

import com.sun.tools.javac.tree.JCTree;

import javax.lang.model.element.Element;
import java.lang.annotation.ElementType;
import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class NodeRootRecord {
    private final Element element;
    private final Element parentElement;
    private final Set<NodeAnnotationRecord> nodeAnnotationRecords;

    private NodeRootRecord(Builder builder) {
        this.element = builder.element;
        this.parentElement = builder.parentElement;
        this.nodeAnnotationRecords = builder.nodeAnnotationRecords;
    }

    public static Builder Builder(Element element,
                                  Set<NodeAnnotationRecord> nodeAnnotationRecords) {
        return new Builder(element, nodeAnnotationRecords);
    }

    public static Builder Builder(Element element) {
        return new Builder(element);
    }

    public Element getElement() {
        return element;
    }

    public Element getParentElement() {
        return parentElement;
    }

    public Set<NodeAnnotationRecord> getNodeAnnotationRecords() {
        return nodeAnnotationRecords;
    }

    static class Builder {
        private final Element element;
        private final Set<NodeAnnotationRecord> nodeAnnotationRecords = new HashSet<>();
        private Element parentElement = null;

        private Builder(Element element, Set<NodeAnnotationRecord> nodeAnnotationRecords) {
            this.element = element;
            this.nodeAnnotationRecords.addAll(nodeAnnotationRecords);
        }

        private Builder(Element element) {
            this.element = element;
        }

        public Builder addNodeAnnotationRecord(NodeAnnotationRecord nodeAnnotationRecord) {
            this.nodeAnnotationRecords.add(nodeAnnotationRecord);
            return this;
        }

        public Builder addNodeAnnotationRecords(Set<NodeAnnotationRecord> nodeAnnotationRecords) {
            this.nodeAnnotationRecords.addAll(nodeAnnotationRecords);
            return this;
        }

        public Builder addParentElement(Element parentElement) {
            this.parentElement = parentElement;
            return this;
        }

        public NodeRootRecord build() {
            return new NodeRootRecord(this);
        }
    }

    public static class NodeAnnotationRecord {
        private final ElementType elementType;
        private final JCTree.JCModifiers modifiers;
        private final NodeIndexRecord nodeIndexRecord;

        private NodeAnnotationRecord(Builder builder) {
            this.modifiers = builder.modifiers;
            this.elementType = builder.elementType;
            this.nodeIndexRecord = builder.nodeIndexRecord;
        }

        public static Builder builder(ElementType elementType,
                                      JCTree.JCModifiers modifiers,
                                      NodeIndexRecord nodeIndexRecord) {
            return new Builder(elementType, modifiers, nodeIndexRecord);
        }

        public ElementType getElementType() {
            return elementType;
        }

        public JCTree.JCModifiers getModifiers() {
            return modifiers;
        }

        public NodeIndexRecord getNodeIndexRecord() {
            return nodeIndexRecord;
        }

        static class Builder {
            private final ElementType elementType;
            private final JCTree.JCModifiers modifiers;
            private final NodeIndexRecord nodeIndexRecord;

            private Builder(ElementType elementType, JCTree.JCModifiers modifiers, NodeIndexRecord nodeIndexRecord) {
                this.elementType = elementType;
                this.modifiers = modifiers;
                this.nodeIndexRecord = nodeIndexRecord;
            }

            public NodeAnnotationRecord build() {
                return new NodeAnnotationRecord(this);
            }
        }

        public static class NodeIndexRecord {
            private final int membersIndex;
            private final int innerIndex;

            private NodeIndexRecord(Builder builder) {
                this.innerIndex = builder.innerIndex;
                this.membersIndex = builder.membersIndex;
            }

            public static Builder builder(int membersIndex) {
                return new Builder(membersIndex);
            }

            public int getMembersIndex() {
                return membersIndex;
            }

            public int getInnerIndex() {
                return innerIndex;
            }

            static class Builder {
                private final int membersIndex;
                private int innerIndex;

                private Builder(int membersIndex) {
                    this.membersIndex = membersIndex;
                }

                public Builder addInnerIndex(int innerIndex) {
                    this.innerIndex = innerIndex;
                    return this;
                }

                public NodeIndexRecord build() {
                    return new NodeIndexRecord(this);
                }
            }
        }
    }
}