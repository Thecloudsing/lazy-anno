package com.luoan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

enum Mode {
    TEMPLATE,
    EXPRESSION,
    ;
}

@Target({
//        ElementType.TYPE,
//        ElementType.FIELD,
//        ElementType.TYPE_PARAMETER,
//        ElementType.ANNOTATION_TYPE,
//        ElementType.METHOD,
        ElementType.LOCAL_VARIABLE,
//        ElementType.PARAMETER
})
@Retention(RetentionPolicy.SOURCE)
public @interface Intensifier {
    Mode value() default Mode.TEMPLATE;
}