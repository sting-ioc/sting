package sting.performance.benchmarks;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.processing.Processor;
import sting.processor.StingProcessor;

final class CodeSizeBenchmark {
    private static final List<String> GWT_SOURCE_JAR_RUNFILES = List.of(
            "+http_file+dagger__sources/file/com/google/dagger/dagger/2.25.2/dagger-2.25.2-sources.jar",
            "+http_file+javax_inject__sources/file/javax/inject/javax.inject/1/javax.inject-1-sources.jar");

    private CodeSizeBenchmark() {}

    static void run(final BenchmarkCommand command, final PrintStream out) throws Exception {
        final var variant = command.codeSizeVariant();
        final var comparisonId = new ComparisonId(command.stingVersion(), command.daggerVersion());
        final var outputDir = workspaceRelative(command.outputDir());
        final var archiveDir = null == command.archiveDir()
                ? Optional.<Path>empty()
                : Optional.of(workspaceRelative(command.archiveDir()));
        final var properties = runScenario(comparisonId, variant, archiveDir, out);
        final var dataFile = BenchmarkDataFile.create(comparisonId, MetricKind.CODE_SIZE, properties);
        BenchmarkDataValidator.validateCodeSizeVariant(dataFile, variant.id());
        dataFile.writeVariant(outputDir, variant.id());
        out.println("Wrote " + BenchmarkDataFile.path(outputDir, comparisonId, MetricKind.CODE_SIZE));
    }

    static OrderedProperties propertiesFor(
            final ComparisonId comparisonId, final CodeSizeVariant variant, final CodeSizeResults results) {
        final var properties = new OrderedProperties();
        properties.put(BenchmarkDataFile.STING_VERSION_KEY, comparisonId.stingVersion());
        properties.put(BenchmarkDataFile.DAGGER_VERSION_KEY, comparisonId.daggerVersion());
        properties.put(BenchmarkDataFile.GENERATED_BY_KEY, BenchmarkDataFile.GENERATED_BY);
        final var prefix = variant.id() + ".";
        properties.put(prefix + "input.layerCount", String.valueOf(variant.layerCount()));
        properties.put(prefix + "input.nodesPerLayer", String.valueOf(variant.nodesPerLayer()));
        properties.put(prefix + "input.inputsPerNode", String.valueOf(variant.inputsPerNode()));
        properties.put(prefix + "input.eagerCount", String.valueOf(variant.eagerCount()));
        properties.put(prefix + "output.sting.size", String.valueOf(results.stingSize()));
        properties.put(prefix + "output.dagger.size", String.valueOf(results.daggerSize()));
        return properties;
    }

    static void archiveOutputs(
            final Path archiveRoot,
            final CodeSizeVariant variant,
            final String implementation,
            final Path moduleOutput,
            final Path extrasOutput,
            final Path sourceDirectory,
            final long size)
            throws IOException {
        final var archiveDir = archiveRoot.resolve(variant.id()).resolve(implementation);
        Files.createDirectories(archiveDir);
        FileUtil.copyTree(moduleOutput, archiveDir.resolve(moduleOutput.getFileName()));
        if (Files.exists(extrasOutput)) {
            FileUtil.copyTree(extrasOutput, archiveDir.resolve("extras"));
        }
        FileUtil.copyTree(sourceDirectory, archiveDir.resolve("src"));
        final var properties = new OrderedProperties();
        properties.put("output." + implementation + ".size", String.valueOf(size));
        properties.write(archiveDir.resolve("statistics.properties"));
    }

    private static OrderedProperties runScenario(
            final ComparisonId comparisonId,
            final CodeSizeVariant variant,
            final Optional<Path> archiveDir,
            final PrintStream out)
            throws Exception {
        final var workingDirectory = Files.createTempDirectory("sting-code-size-");
        try {
            final var results = new CodeSizeResults(
                    performTests("sting", false, variant, workingDirectory, archiveDir, out),
                    performTests("dagger", true, variant, workingDirectory, archiveDir, out));
            return propertiesFor(comparisonId, variant, results);
        } finally {
            FileUtil.deleteTreeIfExists(workingDirectory);
        }
    }

    private static long performTests(
            final String implementation,
            final boolean dagger,
            final CodeSizeVariant variant,
            final Path workingDirectory,
            final Optional<Path> archiveDir,
            final PrintStream out)
            throws Exception {
        final var scenario = new Scenario(workingDirectory.resolve(implementation + "-src"), variant);
        if (dagger) {
            DaggerSourceGenerator.createDaggerInjectScenarioSource(scenario);
        } else {
            StingSourceGenerator.createStingInjectableScenarioSource(scenario);
        }

        final var classnames = new ArrayList<>(scenario.nodeClassNames());
        classnames.addAll(scenario.injectorClassNames());
        classnames.addAll(scenario.entryClassNames());
        final Supplier<Processor> processorSupplier =
                dagger ? CodeSizeBenchmark::newDaggerProcessor : StingProcessor::new;
        JavacTestEngine.compile(processorSupplier, classnames, scenario.outputDirectory(), scenario.outputDirectory());

        FileUtil.deleteTreeIfExists(workingDirectory.resolve("gwt-unitCache"));
        final var moduleName = "com.example.perf." + implementation + ".Application";
        compileGwt(variant, moduleName, scenario.outputDirectory(), workingDirectory);
        final var moduleOutput = workingDirectory.resolve("war").resolve(moduleName);
        final var jsFile = moduleOutput.resolve(moduleName + ".nocache.js");
        if (!Files.exists(jsFile)) {
            throw new IllegalStateException(
                    "Expected GWT output for module " + moduleName + " at " + jsFile.toAbsolutePath());
        }
        final var size = Files.size(jsFile);
        out.println(variant.id() + " " + implementation + " js file size: " + size);
        if (archiveDir.isPresent()) {
            archiveOutputs(
                    archiveDir.get(),
                    variant,
                    implementation,
                    moduleOutput,
                    workingDirectory.resolve("extras").resolve(moduleName),
                    scenario.outputDirectory(),
                    size);
        }
        return size;
    }

    private static void compileGwt(
            final CodeSizeVariant variant,
            final String moduleName,
            final Path sourceDirectory,
            final Path workingDirectory)
            throws IOException, InterruptedException {
        final var command = new ArrayList<>(List.of(
                javaExecutable(),
                "-cp",
                gwtClasspath(sourceDirectory),
                "com.google.gwt.dev.Compiler",
                "-XdisableClassMetadata",
                "-XdisableCastChecking",
                "-optimize",
                "9",
                "-nocheckAssertions",
                "-XmethodNameDisplayMode",
                "NONE",
                "-logLevel",
                "INFO",
                "-noincremental",
                "-compileReport",
                "-war",
                workingDirectory.resolve("war").toString(),
                "-extra",
                workingDirectory.resolve("extras").toString(),
                moduleName));
        final var processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workingDirectory.toFile());
        processBuilder.redirectErrorStream(true);
        final var process = processBuilder.start();
        final var output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        final var exitCode = process.waitFor();
        if (0 != exitCode) {
            throw new IllegalStateException("GWT compiler failed for mode=code-size variant="
                    + variant.id()
                    + " module="
                    + moduleName
                    + " exit="
                    + exitCode
                    + "\nCommand: "
                    + String.join(" ", command)
                    + "\n"
                    + output);
        }
    }

    private static String javaExecutable() {
        return Path.of(System.getProperty("java.home"), "bin", "java").toString();
    }

    private static String gwtClasspath(final Path sourceDirectory) {
        final var elements = new ArrayList<String>();
        elements.add(sourceDirectory.toString());
        workspaceSourceDirectory().ifPresent(path -> elements.add(path.toString()));
        runfilesRoot()
                .ifPresent(root -> GWT_SOURCE_JAR_RUNFILES.stream()
                        .map(root::resolve)
                        .map(CodeSizeBenchmark::existingPath)
                        .forEach(elements::add));
        final var javaClasspath = System.getProperty("java.class.path", "");
        if (!javaClasspath.isEmpty()) {
            Arrays.stream(javaClasspath.split(File.pathSeparator))
                    .filter(element -> !element.isBlank())
                    .map(CodeSizeBenchmark::absolutePath)
                    .forEach(elements::add);
        }
        return elements.stream().collect(Collectors.joining(File.pathSeparator));
    }

    private static Optional<Path> runfilesRoot() {
        return Arrays.stream(System.getProperty("java.class.path", "").split(File.pathSeparator))
                .map(CodeSizeBenchmark::absolutePath)
                .map(Path::of)
                .map(Path::toString)
                .map(path -> {
                    final var marker = ".runfiles";
                    final var index = path.indexOf(marker);
                    return -1 == index ? null : Path.of(path.substring(0, index + marker.length()));
                })
                .filter(path -> null != path)
                .findFirst();
    }

    private static String existingPath(final Path path) {
        if (!Files.exists(path)) {
            throw new IllegalStateException("Expected GWT source jar in runfiles at " + path);
        }
        return path.toString();
    }

    private static String absolutePath(final String path) {
        return Path.of(path).toAbsolutePath().normalize().toString();
    }

    private static Optional<Path> workspaceSourceDirectory() {
        final var workspaceDirectory = System.getenv("BUILD_WORKSPACE_DIRECTORY");
        if (null == workspaceDirectory || workspaceDirectory.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(Path.of(workspaceDirectory, "core", "src", "main", "java"));
    }

    private static Processor newDaggerProcessor() {
        try {
            final var type = Class.forName("dagger.internal.codegen.ComponentProcessor");
            return (Processor) type.getDeclaredConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to instantiate dagger.internal.codegen.ComponentProcessor", e);
        }
    }

    private static Path workspaceRelative(final Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        final var workspaceDirectory = System.getenv("BUILD_WORKSPACE_DIRECTORY");
        if (null == workspaceDirectory || workspaceDirectory.isBlank()) {
            return path;
        }
        return Path.of(workspaceDirectory).resolve(path);
    }
}
