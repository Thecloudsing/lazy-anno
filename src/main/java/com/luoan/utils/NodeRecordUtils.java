package com.luoan.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class NodeRecordUtils<T> {
    public static <T> void addNodeRecord(String annotationCanonicalName,
                                         T record,
                                         Map<String, Set<T>> nodeSetRecord) {
        if (nodeSetRecord.containsKey(annotationCanonicalName)) {
            nodeSetRecord.get(annotationCanonicalName).add(record);
        } else {
            Set<T> nodeAnnotationRecords = new HashSet<>();
            nodeAnnotationRecords.add(record);
            nodeSetRecord.put(annotationCanonicalName, nodeAnnotationRecords);
        }
    }

    public static <T> void addNodeRecords(String annotationCanonicalName,
                                          Set<T> records,
                                          Map<String, Set<T>> nodeSetRecord) {
        if (nodeSetRecord.containsKey(annotationCanonicalName)) {
            nodeSetRecord.get(annotationCanonicalName).addAll(records);
        } else {
            nodeSetRecord.put(annotationCanonicalName, records);
        }
    }
}
