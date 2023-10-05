package com.luoan.code;

import com.luoan.handler.AbstractHandler;
import com.luoan.handler.factory.AnnotationHandlerFactory;
import com.luoan.scan.NodeRootRecord;
import com.luoan.scan.ScanStrategy;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/2
 */
public class ClassCodeStrategy extends AbstractClassCode {

    public static final ClassCodeStrategy classCodeStrategy = createClassCodeStrategy();

    private static ClassCodeStrategy createClassCodeStrategy() {
        try {
            Class<?> appClass = Class.forName("com.luoan.code.ClassCodeStrategy");
            return (ClassCodeStrategy) appClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected void iteration() {
        ScanStrategy.scanStrategy.getScanRecord().forEach((annotationCanonicalName, nodeRootRecords) -> {
            nodeRootRecords.forEach(nodeRootRecord -> exec(annotationCanonicalName, nodeRootRecord));
        });
    }

    private void exec(String annotationCanonicalName, NodeRootRecord nodeRootRecord) {
        AbstractHandler annotationHandler = AnnotationHandlerFactory.getAnnotationHandler(annotationCanonicalName);
        annotationHandler.exec(nodeRootRecord);
        annotationHandler.process();
    }

    @Override
    public void process() {
        if (!init) return;
        iteration();
        destroyed();
    }

    @Override
    public void destroyed() {
        init = Boolean.FALSE;
    }
}
