package com.luoan.handler.factory;

import com.luoan.core.Configuration;
import com.luoan.handler.AbstractHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/1
 */
public class AnnotationHandlerFactory {
    private static final Map<String, AbstractHandler> annotationAbstractHandlerMap = new HashMap<>();

    static {
        initAnnotationHandler();
    }

    public static void registerHandler(String annotationCanonicalName, AbstractHandler handler) {
        annotationAbstractHandlerMap.put(annotationCanonicalName, handler);
    }

    public static AbstractHandler getAnnotationHandler(String annotationCanonicalName) {
        AbstractHandler abstractHandler = annotationAbstractHandlerMap.get(annotationCanonicalName);
        if (Objects.isNull(abstractHandler))
            throw new RuntimeException("fetch not found abstractHandler");
        return abstractHandler;
    }

    private static void initAnnotationHandler() {
        Set<String> annotationHandlerCanonicalNames = Configuration.config.fetchAnnotationHandlerCanonicalNames();
        classHandlerLoader(annotationHandlerCanonicalNames);
    }

    private static void classHandlerLoader(Set<String> classCanonicalNames) {
        for (String className : classCanonicalNames) {
            try {
                Class<?> annotationHandlerClass = Class.forName(className);
                annotationHandlerClass.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
