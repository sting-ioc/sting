package sting.performance.benchmarks;

import java.nio.file.Path;
import java.util.regex.Pattern;

final class ComparisonId {
    private static final Pattern DIRECTORY_PATTERN = Pattern.compile("sting-(.+)__dagger-(.+)");

    private final String _stingVersion;
    private final String _daggerVersion;

    ComparisonId(final String stingVersion, final String daggerVersion) {
        if (stingVersion.isBlank()) {
            throw new IllegalArgumentException("Sting version must not be blank");
        }
        if (daggerVersion.isBlank()) {
            throw new IllegalArgumentException("Dagger version must not be blank");
        }
        _stingVersion = stingVersion;
        _daggerVersion = daggerVersion;
    }

    String stingVersion() {
        return _stingVersion;
    }

    String daggerVersion() {
        return _daggerVersion;
    }

    String directoryName() {
        return "sting-" + _stingVersion + "__dagger-" + _daggerVersion;
    }

    Path resolve(final Path dataDirectory) {
        return dataDirectory.resolve(directoryName());
    }

    static ComparisonId parseDirectoryName(final String directoryName) {
        final var matcher = DIRECTORY_PATTERN.matcher(directoryName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid comparison directory name: " + directoryName);
        }
        return new ComparisonId(matcher.group(1), matcher.group(2));
    }

    @Override
    public String toString() {
        return directoryName();
    }
}
