package com.luoan.utils;

import com.luoan.core.Configuration;
import com.sun.tools.javac.tree.JCTree;
import javafx.util.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/2
 */
public class AnnotationUtils {
    public final static String DOES_NOT_EXIST = "DoesNotExist";

    /**
     * 获取注解规范名称
     *
     * @param jcAnnotation JC 注解
     * @return {@link String}
     */
    public static String fetchAnnotationCanonicalName(JCTree.JCAnnotation jcAnnotation) {
        String annotationName = jcAnnotation.getAnnotationType().toString();
        return fetchAnnotationCanonicalName(annotationName);
    }

    /**
     * 获取注解规范名称
     *
     * @param annotationName 注解名称
     * @return {@link String}
     */
    public static String fetchAnnotationCanonicalName(String annotationName) {
        Pair<Boolean, String> annotationExist = Configuration.config.isAnnotationExist(annotationName);
        return annotationExist.getKey() ? annotationExist.getValue() : DOES_NOT_EXIST;
    }

    /**
     * 如果存在注解 true
     *
     * @param jcAnnotation JC 注解
     * @return boolean
     */
    public static boolean ifExistAnnotation(JCTree.JCAnnotation jcAnnotation) {
        String annotationName = jcAnnotation.getAnnotationType().toString();
        Pair<Boolean, String> annotationExist = Configuration.config.isAnnotationExist(annotationName);
        return annotationExist.getKey();
    }

    /**
     * 如果指定注解 存在true
     *
     * @param jcAnnotation                     JC 注解
     * @param designateAnnotationCanonicalName 指定注解规范名称
     * @return boolean
     */
    public static boolean ifDesignateAnnotation(JCTree.JCAnnotation jcAnnotation, String designateAnnotationCanonicalName) {
        String annotationCanonicalName = fetchAnnotationCanonicalName(jcAnnotation.getAnnotationType().toString());
        return Objects.equals(annotationCanonicalName, designateAnnotationCanonicalName);
    }

    /**
     * 检查注解 存在true
     *
     * @param jcModifiers JC 修饰符
     * @return boolean
     */
    public static boolean checkAnnotation(JCTree.JCModifiers jcModifiers) {
        return jcModifiers.getAnnotations().stream().anyMatch(AnnotationUtils::ifExistAnnotation);
    }

    /**
     * 检查指定注解 存在true
     *
     * @param jcModifiers     JC 修饰符
     * @param annotationClass 注解类
     * @return boolean
     */
    public static boolean checkDesignateAnnotation(JCTree.JCModifiers jcModifiers, Class<?> annotationClass) {
        return checkDesignateAnnotation(jcModifiers, annotationClass.getCanonicalName());
    }

    /**
     * 检查指定注解 存在true
     *
     * @param jcModifiers                      JC 修饰符
     * @param designateAnnotationCanonicalName 指定注解规范名称
     * @return boolean
     */
    public static boolean checkDesignateAnnotation(JCTree.JCModifiers jcModifiers, String designateAnnotationCanonicalName) {
        return jcModifiers.getAnnotations().stream().anyMatch(predicate -> ifDesignateAnnotation(predicate, designateAnnotationCanonicalName));
    }

    /**
     * 检查注解提取列表
     *
     * @param jcModifiers JC 修饰符
     * @return {@link List}<{@link String}>
     */
    public static List<String> checkAnnotationFetchList(JCTree.JCModifiers jcModifiers) {
        return jcModifiers.getAnnotations().stream().filter(AnnotationUtils::ifExistAnnotation)
                .map(AnnotationUtils::fetchAnnotationCanonicalName)
                .collect(Collectors.toList());
    }
}
