package com.luoan.scan;

import com.luoan.core.Application;

import javax.lang.model.element.Element;
import java.util.Set;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public class ScanStrategy extends AbstractScan {
    public static final ScanStrategy scanStrategy = createScanStrategy();

    private ScanStrategy() {
        super();
    }

    private static ScanStrategy createScanStrategy() {
        try {
            Class<?> appClass = Class.forName("com.luoan.scan.ScanStrategy");
            return (ScanStrategy) appClass.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process() {
        if (!init) return;
        Set<? extends Element> rootElements = Application.application.getRoundEnvironment().getRootElements();
        for (Element element : rootElements) {
            javacTrees.getTree(element).accept(new CustomizationTreeTranslator(element, this));
        }
        destroyed();
    }

    @Override
    public void destroyed() {
        init = Boolean.FALSE;
    }
}
