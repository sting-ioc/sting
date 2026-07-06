package sting.performance.benchmarks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

final class JavacTestEngine {
    private JavacTestEngine() {}

    static long compile(
            final Supplier<Processor> processorSupplier,
            final List<String> classnames,
            final Path inputDir,
            final Path outputDir)
            throws IOException {
        final var compiler = ToolProvider.getSystemJavaCompiler();
        if (null == compiler) {
            throw new IllegalStateException("Build-time benchmark requires a JDK with the system Java compiler");
        }
        final var inputPaths = classnames.stream()
                .map(classname -> inputDir.resolve(classname.replace('.', '/') + ".java"))
                .toList();
        final var diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (var fileManager = compiler.getStandardFileManager(diagnostics, null, null)) {
            final var inputs = fileManager.getJavaFileObjectsFromPaths(inputPaths);
            final var task = compiler.getTask(
                    null,
                    fileManager,
                    diagnostics,
                    List.of(
                            "-implicit:none",
                            "-Asting.format_generated_source=false",
                            "-d",
                            outputDir.toString(),
                            "-cp",
                            classpath(inputDir)),
                    null,
                    inputs);
            task.setProcessors(List.of(processorSupplier.get()));

            System.gc();
            final var start = System.nanoTime();
            final var succeeded = task.call();
            final var duration = System.nanoTime() - start;
            System.gc();
            if (!succeeded) {
                throw new AssertionError(describeFailureDiagnostics(diagnostics));
            }
            return duration;
        }
    }

    private static String classpath(final Path inputDir) {
        final var javaClasspath = System.getProperty("java.class.path", "");
        if (javaClasspath.isEmpty()) {
            return inputDir.toString();
        }
        return inputDir + File.pathSeparator + javaClasspath;
    }

    private static String describeFailureDiagnostics(final DiagnosticCollector<JavaFileObject> diagnosticCollector) {
        return diagnosticCollector.getDiagnostics().stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n", "Compilation produced the following diagnostics:\n", "\n"));
    }
}
