package sting.performance.benchmarks;

enum MetricKind {
    BUILD_TIMES("build-times.properties"),
    CODE_SIZE("code-size.properties");

    private final String _fileName;

    MetricKind(final String fileName) {
        _fileName = fileName;
    }

    String fileName() {
        return _fileName;
    }
}
