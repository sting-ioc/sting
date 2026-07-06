package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.nio.file.Files;
import java.util.List;
import org.testng.annotations.Test;

public final class BenchmarkDataValidatorTest {
    private static final ComparisonId COMPARISON_ID = new ComparisonId("1.0", "2.25.2");

    @Test
    public void legacyBuildTimeValidationDoesNotRequireRawTimingData() {
        final var dataFile = buildTimeFile(false);

        BenchmarkDataValidator.validate(dataFile, ValidationProfile.LEGACY_RENDER);
    }

    @Test
    public void generatedBuildTimeValidationRequiresSummaryTimingData() {
        final var dataFile = buildTimeFile(false);

        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE));

        assertEquals(exception.getMessage(), "Missing required property: tiny.output.sting.all.min");
    }

    @Test
    public void generatedBuildTimeValidationRequiresRawTrialData() {
        final var properties = completeBuildTimeProperties();
        for (final var variant : BenchmarkDataValidator.BUILD_TIME_VARIANTS) {
            properties.put(variant + ".output.sting.all.min", "1000");
            properties.put(variant + ".output.dagger.all.min", "2000");
            properties.put(variant + ".output.sting.incremental.min", "3000");
            properties.put(variant + ".output.dagger.incremental.min", "4000");
        }

        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.BUILD_TIMES, properties);
        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE));

        assertEquals(exception.getMessage(), "Missing required property: tiny.output.sting.all.trial.1");
    }

    @Test
    public void generatedBuildTimeValidationPassesWithSummaryAndRawTrialData() {
        final var dataFile = buildTimeFile(true);

        BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE);
    }

    @Test
    public void validationRequiresMetadata() {
        final var properties = buildTimePropertiesWithoutMetadata();
        properties.put(BenchmarkDataFile.STING_VERSION_KEY, COMPARISON_ID.stingVersion());
        properties.put(BenchmarkDataFile.DAGGER_VERSION_KEY, COMPARISON_ID.daggerVersion());
        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.BUILD_TIMES, properties);

        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.LEGACY_RENDER));

        assertEquals(exception.getMessage(), "Missing required metadata: generated.by");
    }

    @Test
    public void validationRequiresAllBuildTimeVariants() {
        final var properties = withMetadata();
        addBuildTimeVariant(properties, "tiny", false);
        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.BUILD_TIMES, properties);

        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.LEGACY_RENDER));

        assertEquals(exception.getMessage(), "Missing required property: small.input.warmupTimeInSeconds");
    }

    @Test
    public void validationRequiresAllCodeSizeKeys() {
        final var properties = completeCodeSizeProperties();
        final var removedValue = properties.asMap().get("tiny.output.dagger.size");
        final var reducedProperties = withMetadata();
        for (final var entry : properties.asMap().entrySet()) {
            if (!isMetadataKey(entry.getKey()) && !"tiny.output.dagger.size".equals(entry.getKey())) {
                reducedProperties.put(entry.getKey(), entry.getValue());
            }
        }
        assertEquals(removedValue, "200");
        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.CODE_SIZE, reducedProperties);

        final var exception = expectThrows(
                ValidationException.class,
                () -> BenchmarkDataValidator.validate(dataFile, ValidationProfile.LEGACY_RENDER));

        assertEquals(exception.getMessage(), "Missing required property: tiny.output.dagger.size");
    }

    @Test
    public void orderedPropertiesRoundTripPreservesOrder() throws Exception {
        final var properties = new OrderedProperties();
        properties.put("b", "2");
        properties.put("a", "1");
        final var path = Files.createTempFile("benchmark-data", ".properties");
        try {
            properties.write(path);

            final var loaded = OrderedProperties.read(path);

            assertEquals(loaded.keySet().stream().toList(), List.of("b", "a"));
        } finally {
            assertTrue(Files.deleteIfExists(path));
        }
    }

    private static BenchmarkDataFile buildTimeFile(final boolean generatedRelease) {
        final var properties = completeBuildTimeProperties();
        if (generatedRelease) {
            for (final var variant : BenchmarkDataValidator.BUILD_TIME_VARIANTS) {
                properties.put(variant + ".output.sting.all.min", "1000");
                properties.put(variant + ".output.dagger.all.min", "2000");
                properties.put(variant + ".output.sting.incremental.min", "3000");
                properties.put(variant + ".output.dagger.incremental.min", "4000");
                for (int i = 1; i <= 2; i++) {
                    properties.put(variant + ".output.sting.all.trial." + i, "1000");
                    properties.put(variant + ".output.dagger.all.trial." + i, "2000");
                    properties.put(variant + ".output.sting.incremental.trial." + i, "3000");
                    properties.put(variant + ".output.dagger.incremental.trial." + i, "4000");
                }
            }
        }
        return BenchmarkDataFile.create(COMPARISON_ID, MetricKind.BUILD_TIMES, properties);
    }

    private static OrderedProperties completeBuildTimeProperties() {
        final var properties = withMetadata();
        for (final var variant : BenchmarkDataValidator.BUILD_TIME_VARIANTS) {
            addBuildTimeVariant(properties, variant, true);
        }
        return properties;
    }

    private static OrderedProperties buildTimePropertiesWithoutMetadata() {
        final var properties = new OrderedProperties();
        for (final var variant : BenchmarkDataValidator.BUILD_TIME_VARIANTS) {
            addBuildTimeVariant(properties, variant, true);
        }
        return properties;
    }

    private static void addBuildTimeVariant(
            final OrderedProperties properties, final String variant, final boolean twoTrials) {
        properties.put(variant + ".input.warmupTimeInSeconds", "20");
        properties.put(variant + ".input.measureTrials", twoTrials ? "2" : "1");
        properties.put(variant + ".input.layerCount", "2");
        properties.put(variant + ".input.nodesPerLayer", "5");
        properties.put(variant + ".input.inputsPerNode", "5");
        properties.put(variant + ".input.eagerCount", "5");
        properties.put(variant + ".output.sting2dagger.all.min", "0.500");
        properties.put(variant + ".output.sting2dagger.incremental.min", "0.250");
    }

    private static OrderedProperties completeCodeSizeProperties() {
        final var properties = withMetadata();
        for (final var variant : BenchmarkDataValidator.CODE_SIZE_VARIANTS) {
            properties.put(variant + ".input.layerCount", "2");
            properties.put(variant + ".input.nodesPerLayer", "5");
            properties.put(variant + ".input.inputsPerNode", "5");
            properties.put(variant + ".input.eagerCount", "5");
            properties.put(variant + ".output.sting.size", "100");
            properties.put(variant + ".output.dagger.size", "200");
        }
        return properties;
    }

    private static OrderedProperties withMetadata() {
        final var properties = new OrderedProperties();
        properties.put(BenchmarkDataFile.STING_VERSION_KEY, COMPARISON_ID.stingVersion());
        properties.put(BenchmarkDataFile.DAGGER_VERSION_KEY, COMPARISON_ID.daggerVersion());
        properties.put(BenchmarkDataFile.GENERATED_BY_KEY, BenchmarkDataFile.GENERATED_BY);
        return properties;
    }

    private static boolean isMetadataKey(final String key) {
        return BenchmarkDataFile.STING_VERSION_KEY.equals(key)
                || BenchmarkDataFile.DAGGER_VERSION_KEY.equals(key)
                || BenchmarkDataFile.GENERATED_BY_KEY.equals(key);
    }
}
