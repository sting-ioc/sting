package sting.performance.benchmarks;

enum CodeSizeVariant {
    EAGER_TINY("eager_tiny", 2, 5, 1.0D),
    TINY("tiny", 2, 5, 0.5D),
    LAZY_TINY("lazy_tiny", 2, 5, 0.0D),
    EAGER_SMALL("eager_small", 5, 10, 1.0D),
    SMALL("small", 5, 10, 0.5D),
    LAZY_SMALL("lazy_small", 5, 10, 0.0D),
    EAGER_MEDIUM("eager_medium", 5, 50, 1.0D),
    MEDIUM("medium", 5, 50, 0.5D),
    LAZY_MEDIUM("lazy_medium", 5, 50, 0.0D),
    EAGER_LARGE("eager_large", 5, 100, 1.0D),
    LARGE("large", 5, 100, 0.5D),
    LAZY_LARGE("lazy_large", 5, 100, 0.0D),
    EAGER_HUGE("eager_huge", 10, 100, 1.0D),
    HUGE("huge", 10, 100, 0.5D),
    LAZY_HUGE("lazy_huge", 10, 100, 0.0D);

    private static final int INPUTS_PER_NODE = 5;

    private final String _id;
    private final int _layerCount;
    private final int _nodesPerLayer;
    private final double _eagerCountRatio;

    CodeSizeVariant(final String id, final int layerCount, final int nodesPerLayer, final double eagerCountRatio) {
        _id = id;
        _layerCount = layerCount;
        _nodesPerLayer = nodesPerLayer;
        _eagerCountRatio = eagerCountRatio;
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
        return (int) ((_layerCount * _nodesPerLayer) * _eagerCountRatio);
    }

    static CodeSizeVariant parse(final String value) {
        for (final var variant : values()) {
            if (variant.id().equals(value)) {
                return variant;
            }
        }
        throw new InvalidCommandLineException(
                "Invalid code-size variant: " + value + ". Expected one of: " + expectedVariants());
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
