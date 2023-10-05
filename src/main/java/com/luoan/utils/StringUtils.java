package com.luoan.utils;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/3
 */
public class StringUtils {
    public static boolean ne(String arg1, String arg2) {
        return !eq(arg1, arg2);
    }

    public static boolean eq(String arg1, String arg2) {
        if (empty(arg1, arg2)) return false;
        return arg1.equals(arg2);
    }

    public static boolean notEmpty(String... args) {
        return !empty(args);
    }

    public static boolean empty(String... args) {
        for (String arg : args)
            if (arg.equals("")) return true;
        return false;
    }

    // =========================================================================== //
    public static boolean ne(Object arg1, Object arg2) {
        return !eq(arg1, arg2);
    }

    public static boolean eq(Object arg1, Object arg2) {
        if (empty(arg1, arg2)) return false;
        return eq(arg1.toString(), arg2.toString());
    }

    public static boolean notEmpty(Object... args) {
        return !empty(args);
    }

    public static boolean empty(Object... args) {
        for (Object arg : args)
            if (arg == null) return true;
        return false;
    }
}
