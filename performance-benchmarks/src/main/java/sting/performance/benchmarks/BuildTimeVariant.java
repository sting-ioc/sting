package sting.performance.benchmarks;

enum BuildTimeVariant {
    TINY("tiny", 2, 5),
    SMALL("small", 5, 10),
    MEDIUM("medium", 5, 50),
    LARGE("large", 5, 100),
    HUGE("huge", 10, 100);

    private static final int INPUTS_PER_NODE = 5;

    private final String _id;
    private final int _layerCount;
    private final int _nodesPerLayer;

    BuildTimeVariant(final String id, final int layerCount, final int nodesPerLayer) {
        _id = id;
        _layerCount = layerCount;
        _nodesPerLayer = nodesPerLayer;
    }

    String id() {
        return _id;
    }

    int layerCount() {
        return _layerCount;
    }

    int nodesPerLayer() {
        return _nodesPerLayer;
    }

    int inputsPerNode() {
        return INPUTS_PER_NODE;
    }

    int eagerCount() {
        return (_layerCount * _nodesPerLayer) / 2;
    }

    static BuildTimeVariant parse(final String value) {
        for (final var variant : values()) {
            if (variant.id().equals(value)) {
                return variant;
            }
        }
        throw new InvalidCommandLineException(
                "Invalid build-time variant: " + value + ". Expected one of: " + expectedVariants());
    }

    static String expectedVariants() {
        final var builder = new StringBuilder();
        for (final var variant : values()) {
            if (0 != builder.length()) {
                builder.append(", ");
            }
            builder.append(variant.id());
        }
        return builder.toString();
    }
}
