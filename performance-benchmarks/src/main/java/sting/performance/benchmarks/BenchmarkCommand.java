package sting.performance.benchmarks;

import java.nio.file.Path;
import java.util.Set;
import org.jspecify.annotations.Nullable;

final class BenchmarkCommand {
    private static final String MODE_ARGUMENT = "--mode";
    private static final String VARIANT_ARGUMENT = "--variant";
    private static final String WARMUP_SECONDS_ARGUMENT = "--warmup-seconds";
    private static final String TRIALS_ARGUMENT = "--trials";
    private static final String OUTPUT_DIR_ARGUMENT = "--output-dir";
    private static final String STING_VERSION_ARGUMENT = "--sting-version";
    private static final String DAGGER_VERSION_ARGUMENT = "--dagger-version";
    private static final String ARCHIVE_DIR_ARGUMENT = "--archive-dir";
    private static final String DATA_DIR_ARGUMENT = "--data-dir";
    private static final String BUILD_TIMES_COMPARISON_ARGUMENT = "--build-times-comparison";
    private static final String CODE_SIZE_COMPARISON_ARGUMENT = "--code-size-comparison";
    private static final String BUILD_TIMES_OUTPUT_ARGUMENT = "--build-times-output";
    private static final String CODE_SIZE_OUTPUT_ARGUMENT = "--code-size-output";
    private static final String BUILD_TIMES_PROFILE_ARGUMENT = "--build-times-profile";
    private static final Set<String> KNOWN_ARGUMENTS = Set.of(
            MODE_ARGUMENT,
            VARIANT_ARGUMENT,
            WARMUP_SECONDS_ARGUMENT,
            TRIALS_ARGUMENT,
            OUTPUT_DIR_ARGUMENT,
            STING_VERSION_ARGUMENT,
            DAGGER_VERSION_ARGUMENT,
            ARCHIVE_DIR_ARGUMENT,
            DATA_DIR_ARGUMENT,
            BUILD_TIMES_COMPARISON_ARGUMENT,
            CODE_SIZE_COMPARISON_ARGUMENT,
            BUILD_TIMES_OUTPUT_ARGUMENT,
            CODE_SIZE_OUTPUT_ARGUMENT,
            BUILD_TIMES_PROFILE_ARGUMENT);

    private final BenchmarkMode _mode;
    private final @Nullable BuildTimeVariant _buildTimeVariant;
    private final @Nullable CodeSizeVariant _codeSizeVariant;
    private final int _warmupSeconds;
    private final int _trials;
    private final Path _outputDir;
    private final String _stingVersion;
    private final String _daggerVersion;
    private final @Nullable Path _archiveDir;
    private final Path _dataDir;
    private final @Nullable ComparisonId _buildTimesComparison;
    private final @Nullable ComparisonId _codeSizeComparison;
    private final @Nullable Path _buildTimesOutput;
    private final @Nullable Path _codeSizeOutput;
    private final ValidationProfile _buildTimesProfile;

    private BenchmarkCommand(
            final BenchmarkMode mode,
            final @Nullable BuildTimeVariant buildTimeVariant,
            final @Nullable CodeSizeVariant codeSizeVariant,
            final int warmupSeconds,
            final int trials,
            final Path outputDir,
            final String stingVersion,
            final String daggerVersion,
            final @Nullable Path archiveDir,
            final Path dataDir,
            final @Nullable ComparisonId buildTimesComparison,
            final @Nullable ComparisonId codeSizeComparison,
            final @Nullable Path buildTimesOutput,
            final @Nullable Path codeSizeOutput,
            final ValidationProfile buildTimesProfile) {
        _mode = mode;
        _buildTimeVariant = buildTimeVariant;
        _codeSizeVariant = codeSizeVariant;
        _warmupSeconds = warmupSeconds;
        _trials = trials;
        _outputDir = outputDir;
        _stingVersion = stingVersion;
        _daggerVersion = daggerVersion;
        _archiveDir = archiveDir;
        _dataDir = dataDir;
        _buildTimesComparison = buildTimesComparison;
        _codeSizeComparison = codeSizeComparison;
        _buildTimesOutput = buildTimesOutput;
        _codeSizeOutput = codeSizeOutput;
        _buildTimesProfile = buildTimesProfile;
    }

    BenchmarkMode mode() {
        return _mode;
    }

    @Nullable
    BuildTimeVariant variant() {
        return _buildTimeVariant;
    }

    @Nullable
    CodeSizeVariant codeSizeVariant() {
        return _codeSizeVariant;
    }

    int warmupSeconds() {
        return _warmupSeconds;
    }

    int trials() {
        return _trials;
    }

    Path outputDir() {
        return _outputDir;
    }

    String stingVersion() {
        return _stingVersion;
    }

    String daggerVersion() {
        return _daggerVersion;
    }

    @Nullable
    Path archiveDir() {
        return _archiveDir;
    }

    Path dataDir() {
        return _dataDir;
    }

    @Nullable
    ComparisonId buildTimesComparison() {
        return _buildTimesComparison;
    }

    @Nullable
    ComparisonId codeSizeComparison() {
        return _codeSizeComparison;
    }

    @Nullable
    Path buildTimesOutput() {
        return _buildTimesOutput;
    }

    @Nullable
    Path codeSizeOutput() {
        return _codeSizeOutput;
    }

    ValidationProfile buildTimesProfile() {
        return _buildTimesProfile;
    }

    static BenchmarkCommand parse(final String[] args) {
        BenchmarkMode mode = null;
        String variant = null;
        int warmupSeconds = BuildTimeBenchmark.DEFAULT_WARMUP_SECONDS;
        int trials = BuildTimeBenchmark.DEFAULT_TRIALS;
        Path outputDir = BuildTimeBenchmark.DEFAULT_DATA_DIRECTORY;
        String stingVersion = null;
        String daggerVersion = null;
        boolean warmupSecondsSeen = false;
        boolean trialsSeen = false;
        boolean outputDirSeen = false;
        boolean stingVersionSeen = false;
        boolean daggerVersionSeen = false;
        boolean archiveDirSeen = false;
        Path archiveDir = null;
        Path dataDir = BuildTimeBenchmark.DEFAULT_DATA_DIRECTORY;
        boolean dataDirSeen = false;
        String buildTimesComparison = null;
        String codeSizeComparison = null;
        Path buildTimesOutput = null;
        Path codeSizeOutput = null;
        ValidationProfile buildTimesProfile = ValidationProfile.LEGACY_RENDER;
        boolean buildTimesComparisonSeen = false;
        boolean codeSizeComparisonSeen = false;
        boolean buildTimesOutputSeen = false;
        boolean codeSizeOutputSeen = false;
        boolean buildTimesProfileSeen = false;
        for (int i = 0; i < args.length; i++) {
            final var parsedArgument = parseArgument(args, i);
            if (parsedArgument.consumedSeparateValue()) {
                i++;
            }
            final var arg = parsedArgument.name();
            final var value = parsedArgument.value();
            if (MODE_ARGUMENT.equals(arg)) {
                if (null != mode) {
                    throw new InvalidCommandLineException("Duplicate argument: " + MODE_ARGUMENT);
                }
                mode = BenchmarkMode.parse(value);
            } else if (VARIANT_ARGUMENT.equals(arg)) {
                if (null != variant) {
                    throw new InvalidCommandLineException("Duplicate argument: " + VARIANT_ARGUMENT);
                }
                variant = value;
            } else if (WARMUP_SECONDS_ARGUMENT.equals(arg)) {
                if (warmupSecondsSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + WARMUP_SECONDS_ARGUMENT);
                }
                warmupSecondsSeen = true;
                warmupSeconds = nonNegativeInteger(WARMUP_SECONDS_ARGUMENT, value);
            } else if (TRIALS_ARGUMENT.equals(arg)) {
                if (trialsSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + TRIALS_ARGUMENT);
                }
                trialsSeen = true;
                trials = positiveInteger(TRIALS_ARGUMENT, value);
            } else if (OUTPUT_DIR_ARGUMENT.equals(arg)) {
                if (outputDirSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + OUTPUT_DIR_ARGUMENT);
                }
                outputDirSeen = true;
                outputDir = Path.of(value);
            } else if (STING_VERSION_ARGUMENT.equals(arg)) {
                if (stingVersionSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + STING_VERSION_ARGUMENT);
                }
                stingVersionSeen = true;
                stingVersion = value;
            } else if (DAGGER_VERSION_ARGUMENT.equals(arg)) {
                if (daggerVersionSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + DAGGER_VERSION_ARGUMENT);
                }
                daggerVersionSeen = true;
                daggerVersion = value;
            } else if (ARCHIVE_DIR_ARGUMENT.equals(arg)) {
                if (archiveDirSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + ARCHIVE_DIR_ARGUMENT);
                }
                archiveDirSeen = true;
                archiveDir = Path.of(value);
            } else if (DATA_DIR_ARGUMENT.equals(arg)) {
                if (dataDirSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + DATA_DIR_ARGUMENT);
                }
                dataDirSeen = true;
                dataDir = Path.of(value);
            } else if (BUILD_TIMES_COMPARISON_ARGUMENT.equals(arg)) {
                if (buildTimesComparisonSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + BUILD_TIMES_COMPARISON_ARGUMENT);
                }
                buildTimesComparisonSeen = true;
                buildTimesComparison = value;
            } else if (CODE_SIZE_COMPARISON_ARGUMENT.equals(arg)) {
                if (codeSizeComparisonSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + CODE_SIZE_COMPARISON_ARGUMENT);
                }
                codeSizeComparisonSeen = true;
                codeSizeComparison = value;
            } else if (BUILD_TIMES_OUTPUT_ARGUMENT.equals(arg)) {
                if (buildTimesOutputSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + BUILD_TIMES_OUTPUT_ARGUMENT);
                }
                buildTimesOutputSeen = true;
                buildTimesOutput = Path.of(value);
            } else if (CODE_SIZE_OUTPUT_ARGUMENT.equals(arg)) {
                if (codeSizeOutputSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + CODE_SIZE_OUTPUT_ARGUMENT);
                }
                codeSizeOutputSeen = true;
                codeSizeOutput = Path.of(value);
            } else if (BUILD_TIMES_PROFILE_ARGUMENT.equals(arg)) {
                if (buildTimesProfileSeen) {
                    throw new InvalidCommandLineException("Duplicate argument: " + BUILD_TIMES_PROFILE_ARGUMENT);
                }
                buildTimesProfileSeen = true;
                buildTimesProfile = ValidationProfile.parse(value);
            } else {
                throw new InvalidCommandLineException("Unknown argument: " + arg);
            }
        }
        if (null == mode) {
            throw new InvalidCommandLineException("Missing required argument: " + MODE_ARGUMENT);
        }
        BuildTimeVariant buildTimeVariant = null;
        CodeSizeVariant codeSizeVariant = null;
        if (BenchmarkMode.BUILD_TIMES == mode) {
            if (null == variant) {
                throw new InvalidCommandLineException("Missing required argument: " + VARIANT_ARGUMENT);
            }
            if (null == stingVersion) {
                throw new InvalidCommandLineException("Missing required argument: " + STING_VERSION_ARGUMENT);
            }
            if (null == daggerVersion) {
                throw new InvalidCommandLineException("Missing required argument: " + DAGGER_VERSION_ARGUMENT);
            }
            buildTimeVariant = BuildTimeVariant.parse(variant);
        } else if (BenchmarkMode.CODE_SIZE == mode) {
            if (null == variant) {
                throw new InvalidCommandLineException("Missing required argument: " + VARIANT_ARGUMENT);
            }
            if (!outputDirSeen) {
                throw new InvalidCommandLineException("Missing required argument: " + OUTPUT_DIR_ARGUMENT);
            }
            if (null == stingVersion) {
                throw new InvalidCommandLineException("Missing required argument: " + STING_VERSION_ARGUMENT);
            }
            if (null == daggerVersion) {
                throw new InvalidCommandLineException("Missing required argument: " + DAGGER_VERSION_ARGUMENT);
            }
            codeSizeVariant = CodeSizeVariant.parse(variant);
        } else if (BenchmarkMode.RENDER_TABLES == mode) {
            if (null == buildTimesComparison
                    && null == buildTimesOutput
                    && null == codeSizeComparison
                    && null == codeSizeOutput) {
                throw new InvalidCommandLineException(
                        "Missing table selection: provide build-time and/or code-size comparison and output");
            }
            if ((null == buildTimesComparison) != (null == buildTimesOutput)) {
                throw new InvalidCommandLineException("Build-time table rendering requires both "
                        + BUILD_TIMES_COMPARISON_ARGUMENT
                        + " and "
                        + BUILD_TIMES_OUTPUT_ARGUMENT);
            }
            if ((null == codeSizeComparison) != (null == codeSizeOutput)) {
                throw new InvalidCommandLineException("Code-size table rendering requires both "
                        + CODE_SIZE_COMPARISON_ARGUMENT
                        + " and "
                        + CODE_SIZE_OUTPUT_ARGUMENT);
            }
        }
        return new BenchmarkCommand(
                mode,
                buildTimeVariant,
                codeSizeVariant,
                warmupSeconds,
                trials,
                outputDir,
                stingVersion,
                daggerVersion,
                archiveDir,
                dataDir,
                null == buildTimesComparison ? null : ComparisonId.parseDirectoryName(buildTimesComparison),
                null == codeSizeComparison ? null : ComparisonId.parseDirectoryName(codeSizeComparison),
                buildTimesOutput,
                codeSizeOutput,
                buildTimesProfile);
    }

    static String usage() {
        return "Usage: benchmark --mode=<"
                + BenchmarkMode.expectedModes()
                + "> [--variant=<build-time: "
                + BuildTimeVariant.expectedVariants()
                + "; code-size: "
                + CodeSizeVariant.expectedVariants()
                + ">] [--warmup-seconds=<seconds>] [--trials=<count>] [--output-dir=<path>] "
                + "[--sting-version=<version>] [--dagger-version=<version>] [--archive-dir=<path>] "
                + "[--data-dir=<path>] [--build-times-comparison=<comparison>] "
                + "[--build-times-output=<path>] [--build-times-profile=<"
                + ValidationProfile.expectedProfiles()
                + ">] [--code-size-comparison=<comparison>] [--code-size-output=<path>]";
    }

    private static ParsedArgument parseArgument(final String[] args, final int index) {
        final var arg = args[index];
        final var equalsIndex = arg.indexOf('=');
        if (-1 != equalsIndex) {
            final var name = arg.substring(0, equalsIndex);
            final var value = arg.substring(equalsIndex + 1);
            if (value.isEmpty()) {
                throw new InvalidCommandLineException("Missing value for " + name);
            }
            return new ParsedArgument(name, value, false);
        }
        if (!arg.startsWith("--")) {
            throw new InvalidCommandLineException("Unknown argument: " + arg);
        }
        if (!KNOWN_ARGUMENTS.contains(arg)) {
            throw new InvalidCommandLineException("Unknown argument: " + arg);
        }
        if (index + 1 >= args.length || args[index + 1].startsWith("-")) {
            throw new InvalidCommandLineException("Missing value for " + arg);
        }
        return new ParsedArgument(arg, args[index + 1], true);
    }

    private static int nonNegativeInteger(final String argument, final String value) {
        final var integer = integer(argument, value);
        if (integer < 0) {
            throw new InvalidCommandLineException("Invalid value for " + argument + ": expected non-negative integer");
        }
        return integer;
    }

    private static int positiveInteger(final String argument, final String value) {
        final var integer = integer(argument, value);
        if (integer <= 0) {
            throw new InvalidCommandLineException("Invalid value for " + argument + ": expected positive integer");
        }
        return integer;
    }

    private static int integer(final String argument, final String value) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new InvalidCommandLineException("Invalid value for " + argument + ": expected integer");
        }
    }

    private record ParsedArgument(String name, String value, boolean consumedSeparateValue) {}
}
