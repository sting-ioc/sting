package sting.performance.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class BenchmarkDataFile {
    static final String GENERATED_BY = "bazel-performance-benchmarks";
    static final String STING_VERSION_KEY = "sting.version";
    static final String DAGGER_VERSION_KEY = "dagger.version";
    static final String GENERATED_BY_KEY = "generated.by";

    private final ComparisonId _comparisonId;
    private final MetricKind _metricKind;
    private final OrderedProperties _properties;

    private BenchmarkDataFile(
            final ComparisonId comparisonId, final MetricKind metricKind, final OrderedProperties properties) {
        _comparisonId = comparisonId;
        _metricKind = metricKind;
        _properties = properties;
    }

    ComparisonId comparisonId() {
        return _comparisonId;
    }

    MetricKind metricKind() {
        return _metricKind;
    }

    OrderedProperties properties() {
        return _properties;
    }

    void write(final Path dataDirectory) throws IOException {
        _properties.write(path(dataDirectory, _comparisonId, _metricKind));
    }

    void writeVariant(final Path dataDirectory, final String variant) throws IOException {
        final var path = path(dataDirectory, _comparisonId, _metricKind);
        if (!Files.exists(path)) {
            write(dataDirectory);
            return;
        }
        final var merged = mergeVariantProperties(OrderedProperties.read(path), _properties, variant);
        create(_comparisonId, _metricKind, merged).write(dataDirectory);
    }

    static BenchmarkDataFile read(
            final Path dataDirectory, final ComparisonId comparisonId, final MetricKind metricKind) throws IOException {
        return new BenchmarkDataFile(
                comparisonId, metricKind, OrderedProperties.read(path(dataDirectory, comparisonId, metricKind)));
    }

    static BenchmarkDataFile create(
            final ComparisonId comparisonId, final MetricKind metricKind, final OrderedProperties properties) {
        return new BenchmarkDataFile(comparisonId, metricKind, properties);
    }

    static Path path(final Path dataDirectory, final ComparisonId comparisonId, final MetricKind metricKind) {
        return comparisonId.resolve(dataDirectory).resolve(metricKind.fileName());
    }

    private static OrderedProperties mergeVariantProperties(
            final OrderedProperties existingProperties,
            final OrderedProperties updatedProperties,
            final String variant) {
        final var merged = new OrderedProperties();
        copyMetadata(updatedProperties, merged);
        final var prefix = variant + ".";
        for (final var entry : existingProperties.asMap().entrySet()) {
            if (!isMetadataKey(entry.getKey()) && !entry.getKey().startsWith(prefix)) {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        for (final var entry : updatedProperties.asMap().entrySet()) {
            if (!isMetadataKey(entry.getKey())) {
                merged.put(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    private static void copyMetadata(final OrderedProperties source, final OrderedProperties destination) {
        destination.put(STING_VERSION_KEY, source.get(STING_VERSION_KEY));
        destination.put(DAGGER_VERSION_KEY, source.get(DAGGER_VERSION_KEY));
        destination.put(GENERATED_BY_KEY, source.get(GENERATED_BY_KEY));
    }

    private static boolean isMetadataKey(final String key) {
        return STING_VERSION_KEY.equals(key) || DAGGER_VERSION_KEY.equals(key) || GENERATED_BY_KEY.equals(key);
    }
}
