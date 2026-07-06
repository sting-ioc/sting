package sting.performance.benchmarks;

enum ValidationProfile {
    LEGACY_RENDER("legacy-render"),
    GENERATED_RELEASE("generated-release");

    private final String _id;

    ValidationProfile(final String id) {
        _id = id;
    }

    String id() {
        return _id;
    }

    static ValidationProfile parse(final String value) {
        for (final var profile : values()) {
            if (profile.id().equals(value)) {
                return profile;
            }
        }
        throw new InvalidCommandLineException(
                "Invalid build-time profile: " + value + ". Expected one of: " + expectedProfiles());
    }

    static String expectedProfiles() {
        final var builder = new StringBuilder();
        for (final var profile : values()) {
            if (0 != builder.length()) {
                builder.append(", ");
            }
            builder.append(profile.id());
        }
        return builder.toString();
    }
}
