package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.file.Files;
import org.testng.annotations.Test;

public final class BuildTimeBenchmarkTest {
    private static final ComparisonId COMPARISON_ID = new ComparisonId("0.0-test", "2.25.2");

    @Test
    public void generatedPropertiesContainSelectedVariantRawTrialsAndRatios() {
        final var properties = BuildTimeBenchmark.propertiesFor(
                COMPARISON_ID,
                BuildTimeVariant.TINY,
                0,
                2,
                new BuildTimeResults(
                        new BuildTimeCompilerResults(new long[] {100, 90}, new long[] {50, 40}),
                        new BuildTimeCompilerResults(new long[] {300, 270}, new long[] {120, 80})));
        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.BUILD_TIMES, properties);

        BenchmarkDataValidator.validateBuildTimeVariant(dataFile, "tiny");

        assertEquals(properties.get(BenchmarkDataFile.STING_VERSION_KEY), "0.0-test");
        assertEquals(properties.get(BenchmarkDataFile.DAGGER_VERSION_KEY), "2.25.2");
        assertEquals(properties.get(BenchmarkDataFile.GENERATED_BY_KEY), BenchmarkDataFile.GENERATED_BY);
        assertEquals(properties.get("tiny.input.warmupTimeInSeconds"), "0");
        assertEquals(properties.get("tiny.input.measureTrials"), "2");
        assertEquals(properties.get("tiny.output.sting.all.min"), "90");
        assertEquals(properties.get("tiny.output.sting.all.trial.1"), "100");
        assertEquals(properties.get("tiny.output.sting.all.trial.2"), "90");
        assertEquals(properties.get("tiny.output.dagger.all.min"), "270");
        assertEquals(properties.get("tiny.output.dagger.all.trial.1"), "300");
        assertEquals(properties.get("tiny.output.dagger.all.trial.2"), "270");
        assertEquals(properties.get("tiny.output.sting.incremental.min"), "40");
        assertEquals(properties.get("tiny.output.sting.incremental.trial.1"), "50");
        assertEquals(properties.get("tiny.output.sting.incremental.trial.2"), "40");
        assertEquals(properties.get("tiny.output.dagger.incremental.min"), "80");
        assertEquals(properties.get("tiny.output.dagger.incremental.trial.1"), "120");
        assertEquals(properties.get("tiny.output.dagger.incremental.trial.2"), "80");
        assertEquals(properties.get("tiny.output.sting2dagger.all.min"), "3.000");
        assertEquals(properties.get("tiny.output.sting2dagger.incremental.min"), "2.000");
        assertFalse(properties.containsKey("small.input.warmupTimeInSeconds"));
    }

    @Test
    public void variantWritesMergeIntoExistingDataFile() throws Exception {
        final var dataDirectory = Files.createTempDirectory("build-time-data-");
        try {
            BenchmarkDataFile.create(
                            COMPARISON_ID,
                            MetricKind.BUILD_TIMES,
                            BuildTimeBenchmark.propertiesFor(
                                    COMPARISON_ID,
                                    BuildTimeVariant.TINY,
                                    0,
                                    1,
                                    new BuildTimeResults(
                                            new BuildTimeCompilerResults(new long[] {100}, new long[] {50}),
                                            new BuildTimeCompilerResults(new long[] {300}, new long[] {120}))))
                    .writeVariant(dataDirectory, "tiny");
            BenchmarkDataFile.create(
                            COMPARISON_ID,
                            MetricKind.BUILD_TIMES,
                            BuildTimeBenchmark.propertiesFor(
                                    COMPARISON_ID,
                                    BuildTimeVariant.SMALL,
                                    0,
                                    1,
                                    new BuildTimeResults(
                                            new BuildTimeCompilerResults(new long[] {200}, new long[] {80}),
                                            new BuildTimeCompilerResults(new long[] {400}, new long[] {160}))))
                    .writeVariant(dataDirectory, "small");

            final var properties = BenchmarkDataFile.read(dataDirectory, COMPARISON_ID, MetricKind.BUILD_TIMES)
                    .properties();

            assertTrue(properties.containsKey("tiny.output.sting.all.trial.1"));
            assertTrue(properties.containsKey("small.output.sting.all.trial.1"));
        } finally {
            FileUtil.deleteTreeIfExists(dataDirectory);
        }
    }
}
