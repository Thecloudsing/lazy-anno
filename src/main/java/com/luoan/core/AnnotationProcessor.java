package com.luoan.core;

import com.luoan.code.ClassCodeStrategy;
import com.luoan.scan.ScanStrategy;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes("com.luoan.*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class AnnotationProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "IntensifierAnnotationProcessor init");
        System.out.println("IntensifierAnnotationProcessor init");
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                String.format("processingEnv instanceof JavacProcessingEnvironment ==> %s",
                        processingEnv instanceof JavacProcessingEnvironment));
        System.out.printf("processingEnv instanceof JavacProcessingEnvironment ==> %s%n",
                processingEnv instanceof JavacProcessingEnvironment);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, processingEnv.getClass().toString());
        System.out.println(processingEnv.getClass());

        Application.application.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //初始化应用程序
        Application.application.init(processingEnv, roundEnv);
        //扫描注解
        ScanStrategy.scanStrategy.process();
        //根据注解生成字节码
        ClassCodeStrategy.classCodeStrategy.process();
        return true;
    }


}

