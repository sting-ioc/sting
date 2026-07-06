package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.expectThrows;

import org.testng.annotations.Test;

public final class BenchmarkCommandTest {
    @Test
    public void parseModeWithEqualsSyntax() {
        final var command = BenchmarkCommand.parse(
                new String[] {"--mode=build-times", "--variant=tiny", "--sting-version=1.0", "--dagger-version=2.25.2"
                });

        assertEquals(command.mode(), BenchmarkMode.BUILD_TIMES);
    }

    @Test
    public void parseModeWithSeparateValue() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode",
            "code-size",
            "--variant",
            "tiny",
            "--output-dir",
            "tmp/perf-smoke",
            "--sting-version",
            "0.0-smoke",
            "--dagger-version",
            "2.25.2"
        });

        assertEquals(command.mode(), BenchmarkMode.CODE_SIZE);
    }

    @Test
    public void parseBuildTimeDefaults() {
        final var command = BenchmarkCommand.parse(
                new String[] {"--mode=build-times", "--variant=small", "--sting-version=1.0", "--dagger-version=2.25.2"
                });

        assertEquals(command.mode(), BenchmarkMode.BUILD_TIMES);
        assertEquals(command.variant(), BuildTimeVariant.SMALL);
        assertEquals(command.warmupSeconds(), 20);
        assertEquals(command.trials(), 10);
        assertEquals(command.outputDir().toString(), "performance-benchmarks/data");
        assertEquals(command.stingVersion(), "1.0");
        assertEquals(command.daggerVersion(), "2.25.2");
    }

    @Test
    public void parseBuildTimeOverrides() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode",
            "build-times",
            "--variant",
            "tiny",
            "--warmup-seconds",
            "0",
            "--trials",
            "1",
            "--output-dir",
            "tmp/perf-smoke",
            "--sting-version",
            "0.0-smoke",
            "--dagger-version",
            "2.25.2"
        });

        assertEquals(command.variant(), BuildTimeVariant.TINY);
        assertEquals(command.warmupSeconds(), 0);
        assertEquals(command.trials(), 1);
        assertEquals(command.outputDir().toString(), "tmp/perf-smoke");
        assertEquals(command.stingVersion(), "0.0-smoke");
        assertEquals(command.daggerVersion(), "2.25.2");
    }

    @Test
    public void parseRenderTablesMode() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode=render-tables",
            "--build-times-comparison=sting-0.30__dagger-2.25.2",
            "--build-times-output=tmp/BuildTimesTable.html"
        });

        assertEquals(command.mode(), BenchmarkMode.RENDER_TABLES);
        assertEquals(command.buildTimesComparison().directoryName(), "sting-0.30__dagger-2.25.2");
        assertEquals(command.buildTimesOutput().toString(), "tmp/BuildTimesTable.html");
        assertEquals(command.buildTimesProfile(), ValidationProfile.LEGACY_RENDER);
    }

    @Test
    public void parseCodeSizeRequiredArgumentsWithoutArchive() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode=code-size",
            "--variant=tiny",
            "--output-dir=tmp/perf-smoke",
            "--sting-version=0.0-smoke",
            "--dagger-version=2.25.2"
        });

        assertEquals(command.mode(), BenchmarkMode.CODE_SIZE);
        assertEquals(command.codeSizeVariant(), CodeSizeVariant.TINY);
        assertEquals(command.outputDir().toString(), "tmp/perf-smoke");
        assertEquals(command.stingVersion(), "0.0-smoke");
        assertEquals(command.daggerVersion(), "2.25.2");
        assertNull(command.archiveDir());
    }

    @Test
    public void parseCodeSizeArchiveDir() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode=code-size",
            "--variant=eager_tiny",
            "--output-dir=tmp/perf-smoke",
            "--sting-version=0.0-smoke",
            "--dagger-version=2.25.2",
            "--archive-dir=tmp/perf-archive"
        });

        assertEquals(command.codeSizeVariant(), CodeSizeVariant.EAGER_TINY);
        assertEquals(command.archiveDir().toString(), "tmp/perf-archive");
    }

    @Test
    public void parseRenderTablesWithGeneratedReleaseProfile() {
        final var command = BenchmarkCommand.parse(new String[] {
            "--mode=render-tables",
            "--data-dir=performance-benchmarks/data",
            "--build-times-comparison=sting-1.0__dagger-2.25.2",
            "--build-times-output=tmp/BuildTimesTable.html",
            "--build-times-profile=generated-release",
            "--code-size-comparison=sting-1.0__dagger-2.25.2",
            "--code-size-output=tmp/CodeSizeTable.html"
        });

        assertEquals(command.dataDir().toString(), "performance-benchmarks/data");
        assertEquals(command.buildTimesProfile(), ValidationProfile.GENERATED_RELEASE);
        assertEquals(command.codeSizeComparison().directoryName(), "sting-1.0__dagger-2.25.2");
        assertEquals(command.codeSizeOutput().toString(), "tmp/CodeSizeTable.html");
    }

    @Test
    public void rejectMissingMode() {
        final var exception =
                expectThrows(InvalidCommandLineException.class, () -> BenchmarkCommand.parse(new String[] {}));

        assertEquals(exception.getMessage(), "Missing required argument: --mode");
    }

    @Test
    public void rejectRenderTablesWithoutSelection() {
        final var exception = expectThrows(
                InvalidCommandLineException.class, () -> BenchmarkCommand.parse(new String[] {"--mode=render-tables"}));

        assertEquals(
                exception.getMessage(),
                "Missing table selection: provide build-time and/or code-size comparison and output");
    }

    @Test
    public void rejectPartialBuildTimesRenderSelection() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(
                        new String[] {"--mode=render-tables", "--build-times-comparison=sting-0.30__dagger-2.25.2"}));

        assertEquals(
                exception.getMessage(),
                "Build-time table rendering requires both --build-times-comparison and --build-times-output");
    }

    @Test
    public void rejectMissingBuildTimeVariant() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(
                        new String[] {"--mode=build-times", "--sting-version=1.0", "--dagger-version=2.25.2"}));

        assertEquals(exception.getMessage(), "Missing required argument: --variant");
    }

    @Test
    public void rejectMissingCodeSizeOutputDir() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(new String[] {
                    "--mode=code-size", "--variant=tiny", "--sting-version=1.0", "--dagger-version=2.25.2"
                }));

        assertEquals(exception.getMessage(), "Missing required argument: --output-dir");
    }

    @Test
    public void rejectInvalidBuildTimeVariant() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(new String[] {
                    "--mode=build-times", "--variant=unknown", "--sting-version=1.0", "--dagger-version=2.25.2"
                }));

        assertEquals(
                exception.getMessage(),
                "Invalid build-time variant: unknown. Expected one of: tiny, small, medium, large, huge");
    }

    @Test
    public void rejectInvalidCodeSizeVariant() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(new String[] {
                    "--mode=code-size",
                    "--variant=unknown",
                    "--output-dir=tmp/perf-smoke",
                    "--sting-version=1.0",
                    "--dagger-version=2.25.2"
                }));

        assertEquals(
                exception.getMessage(),
                "Invalid code-size variant: unknown. Expected one of: eager_tiny, tiny, lazy_tiny, eager_small, small, "
                        + "lazy_small, eager_medium, medium, lazy_medium, eager_large, large, lazy_large, eager_huge, "
                        + "huge, lazy_huge");
    }

    @Test
    public void rejectDuplicateBuildTimeArgument() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(new String[] {
                    "--mode=build-times",
                    "--variant=tiny",
                    "--warmup-seconds=0",
                    "--warmup-seconds=1",
                    "--sting-version=1.0",
                    "--dagger-version=2.25.2"
                }));

        assertEquals(exception.getMessage(), "Duplicate argument: --warmup-seconds");
    }

    @Test
    public void rejectInvalidTrialCount() {
        final var exception = expectThrows(
                InvalidCommandLineException.class,
                () -> BenchmarkCommand.parse(new String[] {
                    "--mode=build-times",
                    "--variant=tiny",
                    "--trials=0",
                    "--sting-version=1.0",
                    "--dagger-version=2.25.2"
                }));

        assertEquals(exception.getMessage(), "Invalid value for --trials: expected positive integer");
    }

    @Test
    public void rejectInvalidMode() {
        final var exception = expectThrows(
                InvalidCommandLineException.class, () -> BenchmarkCommand.parse(new String[] {"--mode=unknown"}));

        assertEquals(
                exception.getMessage(),
                "Invalid mode: unknown. Expected one of: build-times, code-size, render-tables");
    }

    @Test
    public void rejectUnknownArgument() {
        final var exception = expectThrows(
                InvalidCommandLineException.class, () -> BenchmarkCommand.parse(new String[] {"--unknown"}));

        assertEquals(exception.getMessage(), "Unknown argument: --unknown");
    }
}
