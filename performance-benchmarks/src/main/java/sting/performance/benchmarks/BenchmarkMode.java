package sting.performance.benchmarks;

enum BenchmarkMode {
    BUILD_TIMES("build-times"),
    CODE_SIZE("code-size"),
    RENDER_TABLES("render-tables");

    private final String _id;

    BenchmarkMode(final String id) {
        _id = id;
    }

    String id() {
        return _id;
    }

    static BenchmarkMode parse(final String value) {
        for (final var mode : values()) {
            if (mode.id().equals(value)) {
                return mode;
            }
        }
        throw new InvalidCommandLineException("Invalid mode: " + value + ". Expected one of: " + expectedModes());
    }

    static String expectedModes() {
        final var builder = new StringBuilder();
        for (final var mode : values()) {
            if (0 != builder.length()) {
                builder.append(", ");
            }
            builder.append(mode.id());
        }
        return builder.toString();
    }
}
