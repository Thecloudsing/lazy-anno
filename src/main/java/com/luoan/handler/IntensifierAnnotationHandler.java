package com.luoan.handler;

import com.luoan.Intensifier;
import com.luoan.core.Application;
import com.luoan.spec.MethodSpec;
import com.luoan.spec.VarSpec;
import com.luoan.utils.KindUtils;
import com.luoan.utils.LambdaTools;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Objects;


/**
 * Description:
 * Author: luoan
 * Date: 2023/10/1
 */
public class IntensifierAnnotationHandler extends AbstractHandler {

    private final Name SpElParserName = names.fromString("SpElParser");
    private final Name HashMapName = names.fromString("HashMap");
    private final Name MapName = names.fromString("Map");
    private final Name mapName = names.fromString("map");
    private final JCTree.JCIdent Object = treeMaker.Ident(names.fromString("Object"));
    private final JCTree.JCIdent String = treeMaker.Ident(names.fromString("String"));
    private final JCTree.JCIdent HashMap = treeMaker.Ident(HashMapName);
    private final JCTree.JCIdent Map = treeMaker.Ident(MapName);
    private final JCTree.JCIdent map = treeMaker.Ident(mapName);

    @Override
    protected void doInit() {
        createSpElClassSource();
    }

    @Override
    protected List<JCTree.JCImport> initImport() {
        ListBuffer<JCTree.JCImport> jcImports = new ListBuffer<>();
        jcImports.add(makerImport("com.luoan.spel", SpElParserName));
        jcImports.add(makerImport("java.util", HashMapName));
        jcImports.add(makerImport("java.util", MapName));
        return jcImports.toList();
    }

    @Override
    protected void classDef(JCTree.JCClassDecl jcClassDecl) {

    }

    @Override
    protected void methodDef(JCTree.JCClassDecl jcClassDecl, int membersIndex) {
    }

    @Override
    protected void globalVar(JCTree.JCClassDecl jcClassDecl, int membersIndex) {

    }

    @Override
    protected void paramVar(JCTree.JCMethodDecl jcMethodDecl, int membersIndex, int innerIndex) {

    }

    @Override
    protected void localVar(JCTree.JCMethodDecl jcMethodDecl, int membersIndex, int innerIndex) {
        ListBuffer<JCTree.JCIdent> currentGlobalVar = super.getCurrentGlobalVar();
        ListBuffer<JCTree.JCStatement> jcStatements = new ListBuffer<>();

        jcMethodDecl.getBody().getStatements().forEach(LambdaTools.forEachWithIndex((jcStatement, index) -> {
            jcStatements.add(jcStatement);
            if (index < innerIndex && KindUtils.eqVariable(jcStatement))
                currentGlobalVar.add(treeMaker.Ident(((JCTree.JCVariableDecl) jcStatement).getName()));
            if (index == innerIndex) {
                JCTree.JCIdent spelIdent = treeMaker.Ident(((JCTree.JCVariableDecl) jcStatement).getName());
                JCTree.JCVariableDecl mapVar = VarSpec.builder(mapName, Map)
                        .addInitVar(newClass(null, List.of(String, Object), HashMap, List.nil(), null))
                        .build();
                mapVar.vartype = treeMaker.TypeApply(Map, List.of(String, Object));
                jcStatements.add(mapVar);
                currentGlobalVar.forEach(jcIdent -> {
                    jcStatements.add(invokeMethod(
                            map,
                            "put",
                            List.of(treeMaker.Literal(jcIdent.toString()), jcIdent))
                    );
                });
                jcStatements.add(
                        invokeMethod(
                                spelIdent,
                                treeMaker.Ident(SpElParserName),
                                "out",
                                List.of(spelIdent, map)
                        )
                );
            }
        }));

        newMethodList.add(
                MethodSpec.builder(jcMethodDecl.getName())
                        .addReturnType(jcMethodDecl.restype)
                        .addParameterList(jcMethodDecl.params)
                        .addModifiers(jcMethodDecl.getModifiers())
                        .addBlock(treeMaker.Block(0, jcStatements.toList()))
                        .build()
        );
        deprecatedMembersIndexSet.add(membersIndex);
    }


    @Override
    protected String getAnnotation() {
        return Intensifier.class.getCanonicalName();
    }

    private void createSpElClassSource() {
        try {
            JavaFileObject sourceFile = filer.createSourceFile("com.luoan.spel.SpElParser");
            try (Writer writer = sourceFile.openWriter();
                 BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         Objects.requireNonNull(Application.classLoader
                                                 .getResource("source/SpElParser.java")).openStream()))
            ) {
                String len;
                while ((len = reader.readLine()) != null)
                    writer.write(len);
            } catch (IOException ignored) {
            }
        } catch (IOException e) {
            throw new RuntimeException("reader or writer found exception !!!");
        }
    }

    @Override
    public void destroyed() {
        jcClassDecl = null;
    }
}
