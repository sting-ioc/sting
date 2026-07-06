package sting.performance.benchmarks;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.processing.Processor;
import sting.processor.StingProcessor;

final class BuildTimeBenchmark {
    static final int DEFAULT_WARMUP_SECONDS = 20;
    static final int DEFAULT_TRIALS = 10;
    static final Path DEFAULT_DATA_DIRECTORY = Path.of("performance-benchmarks", "data");

    private BuildTimeBenchmark() {}

    static void run(final BenchmarkCommand command, final PrintStream out) throws Exception {
        final var variant = command.variant();
        final var comparisonId = new ComparisonId(command.stingVersion(), command.daggerVersion());
        final var outputDir = workspaceRelative(command.outputDir());
        final var properties = runScenario(comparisonId, variant, command.warmupSeconds(), command.trials(), out);
        final var dataFile = BenchmarkDataFile.create(comparisonId, MetricKind.BUILD_TIMES, properties);
        BenchmarkDataValidator.validateBuildTimeVariant(dataFile, variant.id());
        dataFile.writeVariant(outputDir, variant.id());
        out.println("Wrote " + BenchmarkDataFile.path(outputDir, comparisonId, MetricKind.BUILD_TIMES));
    }

    static OrderedProperties propertiesFor(
            final ComparisonId comparisonId,
            final BuildTimeVariant variant,
            final int warmupSeconds,
            final int trials,
            final BuildTimeResults results) {
        final var properties = new OrderedProperties();
        properties.put(BenchmarkDataFile.STING_VERSION_KEY, comparisonId.stingVersion());
        properties.put(BenchmarkDataFile.DAGGER_VERSION_KEY, comparisonId.daggerVersion());
        properties.put(BenchmarkDataFile.GENERATED_BY_KEY, BenchmarkDataFile.GENERATED_BY);
        putVariantProperties(properties, variant, warmupSeconds, trials, results);
        return properties;
    }

    private static OrderedProperties runScenario(
            final ComparisonId comparisonId,
            final BuildTimeVariant variant,
            final int warmupSeconds,
            final int trials,
            final PrintStream out)
            throws Exception {
        final var workingDirectory = Files.createTempDirectory("sting-build-times-");
        try {
            final var results = new BuildTimeResults(
                    performTests(variant.id(), false, variant, warmupSeconds, trials, workingDirectory, out),
                    performTests(variant.id(), true, variant, warmupSeconds, trials, workingDirectory, out));
            return propertiesFor(comparisonId, variant, warmupSeconds, trials, results);
        } finally {
            FileUtil.deleteTreeIfExists(workingDirectory);
        }
    }

    private static BuildTimeCompilerResults performTests(
            final String label,
            final boolean dagger,
            final BuildTimeVariant variant,
            final int warmupSeconds,
            final int trials,
            final Path workingDirectory,
            final PrintStream out)
            throws Exception {
        final var scenario = new Scenario(workingDirectory.resolve(dagger ? "dagger-src" : "sting-src"), variant);
        if (dagger) {
            DaggerSourceGenerator.createDaggerInjectScenarioSource(scenario);
        } else {
            StingSourceGenerator.createStingInjectableScenarioSource(scenario);
        }

        final var allClassNames = new ArrayList<>(scenario.nodeClassNames());
        allClassNames.addAll(scenario.injectorClassNames());
        allClassNames.addAll(scenario.entryClassNames());
        final Supplier<Processor> compiler = dagger ? BuildTimeBenchmark::newDaggerProcessor : StingProcessor::new;
        final var allDurations = compileTrials(
                label + " " + (dagger ? "dagger" : "sting") + " All",
                scenario,
                warmupSeconds,
                trials,
                compiler,
                allClassNames,
                out);

        JavacTestEngine.compile(
                StingProcessor::new, scenario.nodeClassNames(), scenario.outputDirectory(), scenario.outputDirectory());
        final var incrementalDurations = compileTrials(
                label + " " + (dagger ? "dagger" : "sting") + " Incremental",
                scenario,
                warmupSeconds,
                trials,
                compiler,
                scenario.injectorClassNames(),
                out);
        return new BuildTimeCompilerResults(allDurations, incrementalDurations);
    }

    private static Processor newDaggerProcessor() {
        try {
            final var type = Class.forName("dagger.internal.codegen.ComponentProcessor");
            return (Processor) type.getDeclaredConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to instantiate dagger.internal.codegen.ComponentProcessor", e);
        }
    }

    private static long[] compileTrials(
            final String label,
            final Scenario scenario,
            final int warmupSeconds,
            final int trials,
            final Supplier<Processor> processorSupplier,
            final List<String> classnames,
            final PrintStream out)
            throws IOException {
        final var endWarmup = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmupSeconds);
        while (System.currentTimeMillis() < endWarmup) {
            final var duration = compileTrial(scenario, processorSupplier, classnames);
            out.println(label + " Warmup Trial duration: " + duration);
        }
        final var durations = new long[trials];
        for (int i = 0; i < durations.length; i++) {
            final var duration = compileTrial(scenario, processorSupplier, classnames);
            durations[i] = duration;
            out.println(label + " Trial duration: " + duration);
        }
        return durations;
    }

    private static long compileTrial(
            final Scenario scenario, final Supplier<Processor> processorSupplier, final List<String> classnames)
            throws IOException {
        final var outputDir = Files.createTempDirectory("sting-build-time-classes-");
        try {
            return JavacTestEngine.compile(processorSupplier, classnames, scenario.outputDirectory(), outputDir);
        } finally {
            FileUtil.deleteTreeIfExists(outputDir);
        }
    }

    private static void putVariantProperties(
            final OrderedProperties properties,
            final BuildTimeVariant variant,
            final int warmupSeconds,
            final int trials,
            final BuildTimeResults results) {
        final var prefix = variant.id() + ".";
        properties.put(prefix + "input.warmupTimeInSeconds", String.valueOf(warmupSeconds));
        properties.put(prefix + "input.measureTrials", String.valueOf(trials));
        properties.put(prefix + "input.layerCount", String.valueOf(variant.layerCount()));
        properties.put(prefix + "input.nodesPerLayer", String.valueOf(variant.nodesPerLayer()));
        properties.put(prefix + "input.inputsPerNode", String.valueOf(variant.inputsPerNode()));
        properties.put(prefix + "input.eagerCount", String.valueOf(variant.eagerCount()));
        putDurations(properties, prefix + "output.sting.all", results.sting().allDurations());
        putDurations(properties, prefix + "output.dagger.all", results.dagger().allDurations());
        putDurations(
                properties, prefix + "output.sting.incremental", results.sting().incrementalDurations());
        putDurations(
                properties,
                prefix + "output.dagger.incremental",
                results.dagger().incrementalDurations());
        properties.put(
                prefix + "output.sting2dagger.all.min",
                ratio(results.dagger().allDurations(), results.sting().allDurations()));
        properties.put(
                prefix + "output.sting2dagger.incremental.min",
                ratio(results.dagger().incrementalDurations(), results.sting().incrementalDurations()));
    }

    private static void putDurations(final OrderedProperties properties, final String prefix, final long[] durations) {
        final var min = Arrays.stream(durations).min().orElseThrow();
        properties.put(prefix + ".min", String.valueOf(min));
        for (int i = 0; i < durations.length; i++) {
            properties.put(prefix + ".trial." + (i + 1), String.valueOf(durations[i]));
        }
    }

    private static String ratio(final long[] numeratorDurations, final long[] denominatorDurations) {
        final var numerator = Arrays.stream(numeratorDurations).min().orElseThrow();
        final var denominator = Arrays.stream(denominatorDurations).min().orElseThrow();
        return String.format(Locale.ROOT, "%.3f", (double) numerator / denominator);
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
