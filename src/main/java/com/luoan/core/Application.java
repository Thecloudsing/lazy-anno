package com.luoan.core;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.tools.Diagnostic;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/1
 */
public class Application {

    public static final Application application = createApplication();
    public static final ClassLoader classLoader = Application.class.getClassLoader();
    private JavacTrees javacTrees;
    private TreeMaker treeMaker;
    private Names names;
    private Filer filer;
    private ProcessingEnvironment processingEnvironment;
    private RoundEnvironment roundEnvironment;

    private Application() {
    }

    private static Application createApplication() {
        try {
            Class<?> appClass = Class.forName("com.luoan.core.Application");
            return (Application) appClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    protected void init(ProcessingEnvironment processingEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        javacTrees = JavacTrees.instance(processingEnv);
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        filer = processingEnv.getFiler();

    }

    private Object recursionFindInnerClassField(Object obj, Predicate<Object> predicate, int deepLayer) {
        if (deepLayer <= 0) return null;
        List<Object> objects = new ArrayList<>();
        Field[] declaredFields = obj.getClass().getDeclaredFields();
        int index = 0;
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object o = field.get(obj);
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, deepLayer + ":\t" + index + ":\t" + obj.getClass());
                if (predicate.test(o))
                    return o;
                else
                    objects.add(o);
            } catch (IllegalAccessException e) {
                processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "获取异常 ==>" + deepLayer + ":\t" + index + ":\t" + obj.getClass());
            }
            index++;
        }
        for (Object o : objects) {
            Object o1 = recursionFindInnerClassField(o, predicate, --deepLayer);
            if (o1 != null) return o1;
        }
        return null;
    }

    protected void init(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        processingEnvironment = processingEnv;
        roundEnvironment = roundEnv;
    }

    public JavacTrees getJavacTrees() {
        return javacTrees;
    }

    public TreeMaker getTreeMaker() {
        return treeMaker;
    }

    public Names getNames() {
        return names;
    }

    public Filer getFiler() {
        return filer;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }

    public RoundEnvironment getRoundEnvironment() {
        return roundEnvironment;
    }
}
