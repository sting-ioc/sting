package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.expectThrows;

import java.nio.file.Files;
import org.testng.annotations.Test;

public final class BenchmarkTableRendererTest {
    @Test
    public void renderMigratedHistoricalTables() throws Exception {
        final var outputDirectory = Files.createTempDirectory("benchmark-tables-");
        try {
            BenchmarkTableRenderer.run(BenchmarkCommand.parse(new String[] {
                "--mode=render-tables",
                "--data-dir=performance-benchmarks/data",
                "--build-times-comparison=sting-0.30__dagger-2.25.2",
                "--build-times-output=" + outputDirectory.resolve("BuildTimesTable.html"),
                "--code-size-comparison=sting-0.38__dagger-2.25.2",
                "--code-size-output=" + outputDirectory.resolve("CodeSizeTable.html")
            }));

            final var buildTimes = Files.readString(outputDirectory.resolve("BuildTimesTable.html"));
            assertTrue(buildTimes.contains("Build Time Comparison between Sting v0.30 and Dagger v2.25.2"));
            assertTrue(buildTimes.contains("<td>Tiny</td>"));
            assertTrue(buildTimes.contains("<td>0.967</td>"));
            assertEquals(rowCount(buildTimes), 6);

            final var codeSize = Files.readString(outputDirectory.resolve("CodeSizeTable.html"));
            assertTrue(codeSize.contains("Code Size Comparison between Sting v0.38 and Dagger v2.25.2"));
            assertTrue(codeSize.contains("<td>Eager Tiny</td>"));
            assertTrue(codeSize.contains("<td>11598</td>"));
            assertEquals(rowCount(codeSize), 16);
        } finally {
            FileUtil.deleteTreeIfExists(outputDirectory);
        }
    }

    @Test
    public void generatedReleaseBuildTimeProfileRequiresRawHistoricalTimingData() {
        final var output = expectThrows(
                ValidationException.class,
                () -> BenchmarkTableRenderer.run(BenchmarkCommand.parse(new String[] {
                    "--mode=render-tables",
                    "--data-dir=performance-benchmarks/data",
                    "--build-times-comparison=sting-0.30__dagger-2.25.2",
                    "--build-times-output=tmp/BuildTimesTable.html",
                    "--build-times-profile=generated-release"
                })));

        assertEquals(output.getMessage(), "Missing required property: tiny.output.sting.all.min");
    }

    private static int rowCount(final String html) {
        return html.split("<tr>", -1).length - 1;
    }
}
