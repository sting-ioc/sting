package sting.performance.benchmarks;

final class InvalidCommandLineException extends IllegalArgumentException {
    InvalidCommandLineException(final String message) {
        super(message);
    }
}
