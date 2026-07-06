package sting.performance.benchmarks;

import java.io.PrintStream;

/**
 * Command line entry point for the Sting performance benchmark runner.
 */
public final class BenchmarkRunner {
    private BenchmarkRunner() {}

    public static void main(final String[] args) {
        final var exitCode = run(args, System.err);
        if (0 != exitCode) {
            System.exit(exitCode);
        }
    }

    static int run(final String[] args, final PrintStream err) {
        try {
            final var command = BenchmarkCommand.parse(args);
            if (BenchmarkMode.BUILD_TIMES == command.mode()) {
                BuildTimeBenchmark.run(command, err);
                return 0;
            }
            if (BenchmarkMode.CODE_SIZE == command.mode()) {
                CodeSizeBenchmark.run(command, err);
                return 0;
            }
            if (BenchmarkMode.RENDER_TABLES == command.mode()) {
                BenchmarkTableRenderer.run(command);
                return 0;
            }
            err.println("Benchmark mode '" + command.mode().id() + "' is not implemented yet.");
            return 1;
        } catch (final InvalidCommandLineException e) {
            err.println(e.getMessage());
            err.println(BenchmarkCommand.usage());
            return 2;
        } catch (final Exception e) {
            e.printStackTrace(err);
            return 1;
        }
    }
}
