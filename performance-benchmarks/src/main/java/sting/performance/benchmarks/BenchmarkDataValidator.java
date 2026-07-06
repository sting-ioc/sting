package sting.performance.benchmarks;

import java.util.List;

final class BenchmarkDataValidator {
    static final List<String> BUILD_TIME_VARIANTS = List.of("tiny", "small", "medium", "large", "huge");
    static final List<String> CODE_SIZE_VARIANTS = List.of(
            "eager_tiny",
            "tiny",
            "lazy_tiny",
            "eager_small",
            "small",
            "lazy_small",
            "eager_medium",
            "medium",
            "lazy_medium",
            "eager_large",
            "large",
            "lazy_large",
            "eager_huge",
            "huge",
            "lazy_huge");

    private static final List<String> BUILD_TIME_REQUIRED_SUFFIXES = List.of(
            "input.warmupTimeInSeconds",
            "input.measureTrials",
            "input.layerCount",
            "input.nodesPerLayer",
            "input.inputsPerNode",
            "input.eagerCount",
            "output.sting2dagger.all.min",
            "output.sting2dagger.incremental.min");
    private static final List<String> BUILD_TIME_GENERATED_REQUIRED_SUFFIXES = List.of(
            "output.sting.all.min",
            "output.dagger.all.min",
            "output.sting.incremental.min",
            "output.dagger.incremental.min");
    private static final List<String> BUILD_TIME_TRIAL_SUFFIXES = List.of(
            "output.sting.all.trial.",
            "output.dagger.all.trial.",
            "output.sting.incremental.trial.",
            "output.dagger.incremental.trial.");
    private static final List<String> CODE_SIZE_REQUIRED_SUFFIXES = List.of(
            "input.layerCount",
            "input.nodesPerLayer",
            "input.inputsPerNode",
            "input.eagerCount",
            "output.sting.size",
            "output.dagger.size");

    private BenchmarkDataValidator() {}

    static void validate(final BenchmarkDataFile dataFile, final ValidationProfile profile) {
        validateMetadata(dataFile);
        if (MetricKind.BUILD_TIMES == dataFile.metricKind()) {
            validateBuildTimes(dataFile.properties(), profile);
        } else {
            validateCodeSize(dataFile.properties());
        }
    }

    static void validateBuildTimeVariant(final BenchmarkDataFile dataFile, final String variant) {
        if (MetricKind.BUILD_TIMES != dataFile.metricKind()) {
            throw new ValidationException("Expected build-time data file but was: " + dataFile.metricKind());
        }
        if (!BUILD_TIME_VARIANTS.contains(variant)) {
            throw new ValidationException("Unknown build-time variant '" + variant + "'. Expected one of: "
                    + String.join(", ", BUILD_TIME_VARIANTS));
        }
        validateMetadata(dataFile);
        requireNoUnknownVariants(dataFile.properties(), BUILD_TIME_VARIANTS);
        requireKeys(dataFile.properties(), variant, BUILD_TIME_REQUIRED_SUFFIXES);
        requireKeys(dataFile.properties(), variant, BUILD_TIME_GENERATED_REQUIRED_SUFFIXES);
        final var trialCount = positiveInteger(dataFile.properties(), variant + ".input.measureTrials");
        for (int i = 1; i <= trialCount; i++) {
            for (final var suffix : BUILD_TIME_TRIAL_SUFFIXES) {
                requireKey(dataFile.properties(), variant + "." + suffix + i);
            }
        }
    }

    static void validateCodeSizeVariant(final BenchmarkDataFile dataFile, final String variant) {
        if (MetricKind.CODE_SIZE != dataFile.metricKind()) {
            throw new ValidationException("Expected code-size data file but was: " + dataFile.metricKind());
        }
        if (!CODE_SIZE_VARIANTS.contains(variant)) {
            throw new ValidationException("Unknown code-size variant '" + variant + "'. Expected one of: "
                    + String.join(", ", CODE_SIZE_VARIANTS));
        }
        validateMetadata(dataFile);
        requireNoUnknownVariants(dataFile.properties(), CODE_SIZE_VARIANTS);
        requireKeys(dataFile.properties(), variant, CODE_SIZE_REQUIRED_SUFFIXES);
    }

    private static void validateMetadata(final BenchmarkDataFile dataFile) {
        requireValue(
                dataFile.properties(),
                BenchmarkDataFile.STING_VERSION_KEY,
                dataFile.comparisonId().stingVersion());
        requireValue(
                dataFile.properties(),
                BenchmarkDataFile.DAGGER_VERSION_KEY,
                dataFile.comparisonId().daggerVersion());
        requireValue(dataFile.properties(), BenchmarkDataFile.GENERATED_BY_KEY, BenchmarkDataFile.GENERATED_BY);
    }

    private static void validateBuildTimes(final OrderedProperties properties, final ValidationProfile profile) {
        requireNoUnknownVariants(properties, BUILD_TIME_VARIANTS);
        for (final var variant : BUILD_TIME_VARIANTS) {
            requireKeys(properties, variant, BUILD_TIME_REQUIRED_SUFFIXES);
            if (ValidationProfile.GENERATED_RELEASE == profile) {
                requireKeys(properties, variant, BUILD_TIME_GENERATED_REQUIRED_SUFFIXES);
                final var trialCount = positiveInteger(properties, variant + ".input.measureTrials");
                for (int i = 1; i <= trialCount; i++) {
                    for (final var suffix : BUILD_TIME_TRIAL_SUFFIXES) {
                        requireKey(properties, variant + "." + suffix + i);
                    }
                }
            }
        }
    }

    private static void validateCodeSize(final OrderedProperties properties) {
        requireNoUnknownVariants(properties, CODE_SIZE_VARIANTS);
        for (final var variant : CODE_SIZE_VARIANTS) {
            requireKeys(properties, variant, CODE_SIZE_REQUIRED_SUFFIXES);
        }
    }

    private static void requireNoUnknownVariants(final OrderedProperties properties, final List<String> variants) {
        for (final var key : properties.keySet()) {
            if (BenchmarkDataFile.STING_VERSION_KEY.equals(key)
                    || BenchmarkDataFile.DAGGER_VERSION_KEY.equals(key)
                    || BenchmarkDataFile.GENERATED_BY_KEY.equals(key)) {
                continue;
            }
            final var variant = variantName(key);
            if (!variants.contains(variant)) {
                throw new ValidationException("Unknown variant '" + variant + "' in property key: " + key);
            }
        }
    }

    private static void requireKeys(
            final OrderedProperties properties, final String variant, final List<String> requiredSuffixes) {
        for (final var suffix : requiredSuffixes) {
            requireKey(properties, variant + "." + suffix);
        }
    }

    private static void requireKey(final OrderedProperties properties, final String key) {
        if (!properties.containsKey(key)) {
            throw new ValidationException("Missing required property: " + key);
        }
    }

    private static void requireValue(final OrderedProperties properties, final String key, final String expected) {
        final var actual = properties.get(key);
        if (null == actual) {
            throw new ValidationException("Missing required metadata: " + key);
        }
        if (!expected.equals(actual)) {
            throw new ValidationException(
                    "Invalid metadata value for " + key + ": expected '" + expected + "' but was '" + actual + "'");
        }
    }

    private static int positiveInteger(final OrderedProperties properties, final String key) {
        final var value = properties.get(key);
        try {
            final var integer = Integer.parseInt(value);
            if (integer <= 0) {
                throw new ValidationException("Property must be positive: " + key);
            }
            return integer;
        } catch (final NumberFormatException e) {
            throw new ValidationException("Property must be an integer: " + key);
        }
    }

    private static String variantName(final String key) {
        final var index = key.indexOf('.');
        if (-1 == index) {
            throw new ValidationException("Property key is missing variant prefix: " + key);
        }
        return key.substring(0, index);
    }
}
