package com.luoan.utils;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/2
 */

public class LambdaTools {
    public static <T> Consumer<T> forEachWithIndex(BiConsumer<T, Integer> biConsumer) {
        /*这里说明一下，我们每次传入forEach都是一个重新实例化的Consumer对象，在lambada表达式中我们无法对int进行++操作,
        我们模拟AtomicInteger对象，写个getAndIncrement方法，不能直接使用AtomicInteger哦*/
        class IncrementInt {
            int i = 0;

            public int getAndIncrement() {
                return i++;
            }
        }
        IncrementInt incrementInt = new IncrementInt();
        return t -> biConsumer.accept(t, incrementInt.getAndIncrement());
    }

    public static <T> Predicate<T> filterWithIndex(BiPredicate<T, Integer> biPredicate) {
        /*这里说明一下，我们每次传入forEach都是一个重新实例化的Consumer对象，在lambada表达式中我们无法对int进行++操作,
        我们模拟AtomicInteger对象，写个getAndIncrement方法，不能直接使用AtomicInteger哦*/
        class IncrementInt {
            int i = 0;

            public int getAndIncrement() {
                return i++;
            }
        }
        IncrementInt incrementInt = new IncrementInt();
        return t -> biPredicate.test(t, incrementInt.getAndIncrement());
    }
}
