package sting.performance.benchmarks;

import com.google.gwt.core.client.EntryPoint;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterSpec;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javaemul.internal.annotations.DoNotInline;
import javax.lang.model.element.Modifier;
import sting.Eager;
import sting.Injectable;
import sting.Injector;

final class StingSourceGenerator {
    private static final String PKG = "com.example.perf.sting";

    private StingSourceGenerator() {}

    static void createStingInjectableScenarioSource(final Scenario scenario) throws IOException {
        final var outputDirectory = scenario.outputDirectory();
        final var layerCount = scenario.layerCount();
        final var nodesPerLayer = scenario.nodesPerLayer();
        final var inputsPerNode = scenario.inputsPerNode();

        FileUtil.deleteTreeIfExists(outputDirectory);

        int currentInputNode = 0;
        int remainingEager = scenario.eagerCount();
        for (int layer = 0; layer < layerCount; layer++) {
            for (int node = 0; node < nodesPerLayer; node++) {
                final var className = toInjectableClassName(nodesPerLayer, layer, node);
                final var type = TypeSpec.classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addAnnotation(Injectable.class);
                if (remainingEager > 0) {
                    type.addAnnotation(Eager.class);
                    remainingEager--;
                }
                final var compute = MethodSpec.methodBuilder("compute")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(DoNotInline.class);
                if (0 != layer) {
                    final var constructor = MethodSpec.constructorBuilder();

                    for (int input = 0; input < inputsPerNode; input++) {
                        final var inputType = toInjectableClassName(
                                nodesPerLayer, layer - 1, (currentInputNode + input) % nodesPerLayer);
                        final var name = "input" + input;
                        type.addField(FieldSpec.builder(inputType, name, Modifier.PRIVATE, Modifier.FINAL)
                                .build());
                        constructor.addParameter(ParameterSpec.builder(inputType, name, Modifier.FINAL)
                                .build());
                        constructor.addStatement("this.$N = $N", name, name);
                        compute.addStatement("$N.compute()", name);
                        compute.addStatement("$T.log( $N.hashCode() )", ClassName.get("akasha", "Console"), name);
                    }
                    type.addMethod(constructor.build());
                    currentInputNode = (currentInputNode + inputsPerNode) % nodesPerLayer;
                }
                type.addMethod(compute.build());

                JavaFile.builder(className.packageName(), type.build())
                        .skipJavaLangImports(true)
                        .build()
                        .writeTo(outputDirectory, StandardCharsets.UTF_8);
                scenario.addNodeClassName(className.canonicalName());
            }
        }

        final var applicationClassName = ClassName.get(PKG, "Application");
        final var applicationType = TypeSpec.interfaceBuilder(applicationClassName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Injector.class);

        for (int node = 0; node < nodesPerLayer; node++) {
            final var inputType = toInjectableClassName(nodesPerLayer, layerCount - 1, node);
            applicationType.addMethod(MethodSpec.methodBuilder(inputType.simpleName())
                    .returns(inputType)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
        }
        JavaFile.builder(applicationClassName.packageName(), applicationType.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(outputDirectory, StandardCharsets.UTF_8);
        scenario.addInjectorClassName(applicationClassName.canonicalName());

        final var entrypointClassName = ClassName.get(PKG, "ApplicationEntrypoint");
        final var entrypointType = TypeSpec.classBuilder(entrypointClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(EntryPoint.class);

        final var method = MethodSpec.methodBuilder("onModuleLoad")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);
        method.addStatement("final Application application = new Sting_Application()");
        for (int node = 0; node < nodesPerLayer; node++) {
            final var inputType = toInjectableClassName(nodesPerLayer, layerCount - 1, node);
            method.addStatement("application.$N().compute()", inputType.simpleName());
        }

        entrypointType.addMethod(method.build());

        JavaFile.builder(entrypointClassName.packageName(), entrypointType.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(outputDirectory, StandardCharsets.UTF_8);
        scenario.addEntryClassName(entrypointClassName.canonicalName());
        final var moduleXml = "<module>\n"
                + "  <inherits name='com.google.gwt.core.Core'/>\n"
                + "  <inherits name='akasha.Akasha'/>\n"
                + "  <inherits name='sting.Sting'/>\n"
                + "  <inherits name='org.realityforge.braincheck.BrainCheck'/>\n"
                + "\n"
                + "  <set-property name='jre.checks.checkLevel' value='MINIMAL'/>\n"
                + "  <set-property name='compiler.stackMode' value='strip'/>\n"
                + "\n"
                + "  <entry-point class='com.example.perf.sting.ApplicationEntrypoint'/>\n"
                + "\n"
                + "  <source path=''/>\n"
                + "\n"
                + "  <add-linker name='sso'/>\n"
                + "</module>\n";
        Files.write(
                outputDirectory.resolve(PKG.replace('.', '/')).resolve("Application.gwt.xml"),
                moduleXml.getBytes(StandardCharsets.UTF_8));
    }

    private static ClassName toInjectableClassName(final int nodesPerLayer, final int layer, final int node) {
        final var nodeIndex = (layer * nodesPerLayer) + node + 1;
        return ClassName.get(PKG + ".layer" + (layer + 1), "Node" + nodeIndex);
    }
}
