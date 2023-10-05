package com.luoan.scan;

import com.luoan.core.Application;
import com.luoan.core.Process;
import com.luoan.utils.AnnotationUtils;
import com.luoan.utils.NodeRecordUtils;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public abstract class AbstractScan implements Process {
    protected final Map<String, Set<NodeRootRecord>> scanRecord = new HashMap<>();
    protected JavacTrees javacTrees;
    protected boolean init = Boolean.TRUE;

    protected AbstractScan() {
        init();
    }

    private void init() {
        javacTrees = Application.application.getJavacTrees();
    }

    public Pair<Boolean, List<String>> annotationScan(JCTree.JCModifiers jcModifiers) {
        List<String> annotations = jcModifiers.getAnnotations().stream()
                .filter(AnnotationUtils::ifExistAnnotation)
                .map(AnnotationUtils::fetchAnnotationCanonicalName)
                .collect(Collectors.toList());
        return new Pair<>(annotations.isEmpty(), annotations);
    }

    public void commit(String annotationCanonicalName, NodeRootRecord nodeRootRecord) {
        NodeRecordUtils.addNodeRecord(annotationCanonicalName, nodeRootRecord, scanRecord);
    }

    public void commit(String annotationCanonicalName, Set<NodeRootRecord> nodeRootRecords) {
        NodeRecordUtils.addNodeRecords(annotationCanonicalName, nodeRootRecords, scanRecord);
    }

    public void commit(Map<String, NodeRootRecord> annotationNodeRootRecordMap) {
        annotationNodeRootRecordMap.forEach(this::commit);
    }

    public Map<String, Set<NodeRootRecord>> getScanRecord() {
        return this.scanRecord;
    }
}
