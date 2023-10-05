package com.luoan.spec;

import com.luoan.core.Application;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

/**
 * Description:
 * Author: luoan
 * Date: 2023/10/4
 */
public abstract class AbstractSpec {
    protected TreeMaker treeMaker;
    protected Names names;

    protected AbstractSpec() {
        init();
    }

    protected static Name initName(String name) {
        return Application.application.getNames().fromString(name);
    }

    private void init() {
        Application application = Application.application;
        this.treeMaker = application.getTreeMaker();
        this.names = application.getNames();
    }
}
