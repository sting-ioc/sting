package sting.performance.benchmarks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class BenchmarkTableRenderer {
    private BenchmarkTableRenderer() {}

    static void run(final BenchmarkCommand command) throws IOException {
        final var dataDirectory = workspaceRelative(command.dataDir());
        final var buildTimesComparison = command.buildTimesComparison();
        final var buildTimesOutput = command.buildTimesOutput();
        if (null != buildTimesComparison && null != buildTimesOutput) {
            renderBuildTimes(
                    dataDirectory,
                    buildTimesComparison,
                    workspaceRelative(buildTimesOutput),
                    command.buildTimesProfile());
        }
        final var codeSizeComparison = command.codeSizeComparison();
        final var codeSizeOutput = command.codeSizeOutput();
        if (null != codeSizeComparison && null != codeSizeOutput) {
            renderCodeSize(dataDirectory, codeSizeComparison, workspaceRelative(codeSizeOutput));
        }
    }

    private static void renderBuildTimes(
            final Path dataDirectory,
            final ComparisonId comparisonId,
            final Path outputPath,
            final ValidationProfile validationProfile)
            throws IOException {
        final var dataFile = BenchmarkDataFile.read(dataDirectory, comparisonId, MetricKind.BUILD_TIMES);
        BenchmarkDataValidator.validate(dataFile, validationProfile);
        final var properties = dataFile.properties();
        final var builder = new StringBuilder();
        builder.append("<table>\n");
        builder.append("  <caption align=\"bottom\">Build Time Comparison between Sting v")
                .append(comparisonId.stingVersion())
                .append(" and Dagger v")
                .append(comparisonId.daggerVersion())
                .append("</caption>\n");
        builder.append("  <thead>\n");
        builder.append("  <tr>\n");
        builder.append("    <th>Variant</th>\n");
        builder.append("    <th>Component Count</th>\n");
        builder.append("    <th>Full Compile</th>\n");
        builder.append("    <th>Incremental Compile</th>\n");
        builder.append("  </tr>\n");
        builder.append("  </thead>\n");
        builder.append("  <tbody>\n");
        for (final var variant : BenchmarkDataValidator.BUILD_TIME_VARIANTS) {
            final var layerCount = integer(properties, variant + ".input.layerCount");
            final var nodesPerLayer = integer(properties, variant + ".input.nodesPerLayer");
            builder.append("  <tr>\n");
            builder.append("    <td>").append(label(variant)).append("</td>\n");
            builder.append("    <td>").append(layerCount * nodesPerLayer).append("</td>\n");
            builder.append("    <td>")
                    .append(properties.get(variant + ".output.sting2dagger.all.min"))
                    .append("</td>\n");
            builder.append("    <td>")
                    .append(properties.get(variant + ".output.sting2dagger.incremental.min"))
                    .append("</td>\n");
            builder.append("  </tr>\n");
        }
        builder.append("  </tbody>\n");
        builder.append("</table>\n");
        write(outputPath, builder.toString());
    }

    private static void renderCodeSize(final Path dataDirectory, final ComparisonId comparisonId, final Path outputPath)
            throws IOException {
        final var dataFile = BenchmarkDataFile.read(dataDirectory, comparisonId, MetricKind.CODE_SIZE);
        BenchmarkDataValidator.validate(dataFile, ValidationProfile.GENERATED_RELEASE);
        final var properties = dataFile.properties();
        final var builder = new StringBuilder();
        builder.append("<table>\n");
        builder.append("  <caption align=\"bottom\">Code Size Comparison between Sting v")
                .append(comparisonId.stingVersion())
                .append(" and Dagger v")
                .append(comparisonId.daggerVersion())
                .append("</caption>\n");
        builder.append("  <thead>\n");
        builder.append("  <tr>\n");
        builder.append("    <th>Variant</th>\n");
        builder.append("    <th>Component Count</th>\n");
        builder.append("    <th>Eager %</th>\n");
        builder.append("    <th>Sting Size</th>\n");
        builder.append("    <th>Dagger Size</th>\n");
        builder.append("    <th>Size Delta</th>\n");
        builder.append("  </tr>\n");
        builder.append("  </thead>\n");
        builder.append("  <tbody>\n");
        for (final var variant : BenchmarkDataValidator.CODE_SIZE_VARIANTS) {
            final var eagerCount = integer(properties, variant + ".input.eagerCount");
            final var layerCount = integer(properties, variant + ".input.layerCount");
            final var nodesPerLayer = integer(properties, variant + ".input.nodesPerLayer");
            final var nodeCount = layerCount * nodesPerLayer;
            final var daggerSize = integer(properties, variant + ".output.dagger.size");
            final var stingSize = integer(properties, variant + ".output.sting.size");
            builder.append("  <tr>\n");
            builder.append("    <td>").append(label(variant)).append("</td>\n");
            builder.append("    <td>").append(nodeCount).append("</td>\n");
            builder.append("    <td>").append(eagerCount * 100 / nodeCount).append("%</td>\n");
            builder.append("    <td>").append(stingSize).append("</td>\n");
            builder.append("    <td>").append(daggerSize).append("</td>\n");
            builder.append("    <td>")
                    .append(signedDelta(daggerSize - stingSize))
                    .append("</td>\n");
            builder.append("  </tr>\n");
        }
        builder.append("  </tbody>\n");
        builder.append("</table>\n");
        write(outputPath, builder.toString());
    }

    private static String signedDelta(final int delta) {
        return 0 <= delta ? "+" + delta : String.valueOf(delta);
    }

    private static int integer(final OrderedProperties properties, final String key) {
        return Integer.parseInt(properties.get(key));
    }

    private static String label(final String variant) {
        final var builder = new StringBuilder();
        for (final var part : variant.split("_")) {
            if (0 != builder.length()) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            builder.append(part.substring(1));
        }
        return builder.toString();
    }

    private static void write(final Path outputPath, final String content) throws IOException {
        final var parent = outputPath.getParent();
        if (null != parent) {
            Files.createDirectories(parent);
        }
        Files.writeString(outputPath, content, StandardCharsets.UTF_8);
    }

    private static Path workspaceRelative(final Path path) {
        if (path.isAbsolute()) {
            return path;
        }
        final var workspaceDirectory = System.getenv("BUILD_WORKSPACE_DIRECTORY");
        if (null == workspaceDirectory || workspaceDirectory.isBlank()) {
            return path;
        }
        return Path.of(workspaceDirectory).resolve(path);
    }
}
