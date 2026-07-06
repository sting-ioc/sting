package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.annotations.Test;

public final class MigratedBenchmarkDataTest {
    private static final String DAGGER_VERSION = "2.25.2";
    private static final Path DATA_DIRECTORY = Path.of("performance-benchmarks", "data");
    private static final String[] BUILD_TIME_VERSIONS = {
        "0.05", "0.06", "0.09", "0.15", "0.17", "0.21", "0.25", "0.28", "0.29", "0.30"
    };
    private static final String[] CODE_SIZE_VERSIONS = {
        "0.05", "0.06", "0.07", "0.09", "0.10", "0.14", "0.15", "0.17", "0.21", "0.28", "0.29", "0.30", "0.38"
    };

    @Test
    public void migratedBuildTimeDataValidatesForLegacyRendering() throws Exception {
        for (final var stingVersion : BUILD_TIME_VERSIONS) {
            final var comparisonId = new ComparisonId(stingVersion, DAGGER_VERSION);
            final var dataFile = BenchmarkDataFile.read(DATA_DIRECTORY, comparisonId, MetricKind.BUILD_TIMES);

            BenchmarkDataValidator.validate(dataFile, ValidationProfile.LEGACY_RENDER);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.STING_VERSION_KEY), stingVersion);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.DAGGER_VERSION_KEY), DAGGER_VERSION);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.GENERATED_BY_KEY), BenchmarkDataFile.GENERATED_BY);
            assertFalse(dataFile.properties().containsKey("tiny.output.sting.all.min"));
        }
    }

    @Test
    public void migratedBuildTimeDataDoesNotPretendToBeGeneratedReleaseData() throws Exception {
        final var comparisonId = new ComparisonId("0.30", DAGGER_VERSION);
        final var dataFile = BenchmarkDataFile.read(DATA_DIRECTORY, comparisonId, MetricKind.BUILD_TIMES);

        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE));

        assertEquals(exception.getMessage(), "Missing required property: tiny.output.sting.all.min");
    }

    @Test
    public void migratedCodeSizeDataValidates() throws Exception {
        for (final var stingVersion : CODE_SIZE_VERSIONS) {
            final var comparisonId = new ComparisonId(stingVersion, DAGGER_VERSION);
            final var dataFile = BenchmarkDataFile.read(DATA_DIRECTORY, comparisonId, MetricKind.CODE_SIZE);

            BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.STING_VERSION_KEY), stingVersion);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.DAGGER_VERSION_KEY), DAGGER_VERSION);
            assertEquals(dataFile.properties().get(BenchmarkDataFile.GENERATED_BY_KEY), BenchmarkDataFile.GENERATED_BY);
        }
    }

    @Test
    public void migratedDataUsesPerComparisonLayout() throws Exception {
        int buildTimeCount = 0;
        int codeSizeCount = 0;
        try (var paths = Files.walk(DATA_DIRECTORY)) {
            for (final var path : paths.filter(Files::isRegularFile).toList()) {
                final var comparisonId = ComparisonId.parseDirectoryName(
                        path.getParent().getFileName().toString());
                assertTrue(
                        path.startsWith(comparisonId.resolve(DATA_DIRECTORY)),
                        "Expected " + path + " to live below its comparison directory");
                if (MetricKind.BUILD_TIMES.fileName().equals(path.getFileName().toString())) {
                    buildTimeCount++;
                } else if (MetricKind.CODE_SIZE
                        .fileName()
                        .equals(path.getFileName().toString())) {
                    codeSizeCount++;
                }
            }
        }

        assertEquals(buildTimeCount, BUILD_TIME_VERSIONS.length);
        assertEquals(codeSizeCount, CODE_SIZE_VERSIONS.length);
    }
}
