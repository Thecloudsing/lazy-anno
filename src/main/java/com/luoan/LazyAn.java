package com.luoan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/3
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface LazyAn {
}
