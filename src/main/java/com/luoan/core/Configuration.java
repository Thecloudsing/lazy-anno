package com.luoan.core;

import javafx.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/2
 */
public class Configuration {
    public static final Configuration config = createConfiguration();
    private final Map<String, Map<String, String>> configurationContext = new HashMap<>();
    private final Map<String, String> annotationCanonicalNameMap = new HashMap<>();
    private final Map<String, String> annotationHandlerCanonicalNameMap = new HashMap<>();

    private Configuration() {
        initContext();
        initConfiguration();
    }

    public static void main(String[] args) {
        System.out.println(config.annotationCanonicalNameMap);
        System.out.println(config.annotationHandlerCanonicalNameMap);
    }

    private static Configuration createConfiguration() {
        try {
            Class<?> configurationClass = Class.forName("com.luoan.core.Configuration");
            return (Configuration) configurationClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void initContext() {
        configurationContext.put("annotation.canonical.name.properties", annotationCanonicalNameMap);
        configurationContext.put("annotation.handler.canonical.name.properties", annotationHandlerCanonicalNameMap);
    }

    private void initConfiguration() {
        configurationContext.forEach(this::load);
    }

    private void load(String filename, Map<String, String> map) {
        try {
            InputStream inputStream = Objects
                    .requireNonNull(Application
                            .class
                            .getClassLoader()
                            .getResource(filename))
                    .openStream();
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.keySet().stream()
                    .map(key -> (String) key)
                    .forEach(key -> {
                        collect(map, key, Function.identity(), properties::getProperty);
                    });
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <K, V> void collect(Map<K, V> map, K key, Function<K, K> keyMapper, Function<K, V> valueMapper) {
        map.put(keyMapper.apply(key), valueMapper.apply(key));
    }

    public Pair<Boolean, String> isAnnotationExist(String annotationName) {
        boolean exist = annotationCanonicalNameMap.containsKey(annotationName);
        String annotationCanonicalName = annotationCanonicalNameMap.get(annotationName);
        return new Pair<>(exist, exist ? annotationCanonicalName : "");
    }

    public Set<String> fetchAnnotationHandlerCanonicalNames() {
        return new HashSet<>(annotationHandlerCanonicalNameMap.values());
    }

}
