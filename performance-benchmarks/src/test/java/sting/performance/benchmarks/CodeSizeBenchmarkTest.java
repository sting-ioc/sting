package sting.performance.benchmarks;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import org.testng.annotations.Test;

public final class CodeSizeBenchmarkTest {
    private static final ComparisonId COMPARISON_ID = new ComparisonId("0.0-test", "2.25.2");

    @Test
    public void generatedPropertiesContainSelectedVariantSizes() {
        final var properties =
                CodeSizeBenchmark.propertiesFor(COMPARISON_ID, CodeSizeVariant.TINY, new CodeSizeResults(1234, 2345));
        final var dataFile = BenchmarkDataFile.create(COMPARISON_ID, MetricKind.CODE_SIZE, properties);

        BenchmarkDataValidator.validateCodeSizeVariant(dataFile, "tiny");

        assertEquals(
                properties.keySet().stream().toList(),
                List.of(
                        BenchmarkDataFile.STING_VERSION_KEY,
                        BenchmarkDataFile.DAGGER_VERSION_KEY,
                        BenchmarkDataFile.GENERATED_BY_KEY,
                        "tiny.input.layerCount",
                        "tiny.input.nodesPerLayer",
                        "tiny.input.inputsPerNode",
                        "tiny.input.eagerCount",
                        "tiny.output.sting.size",
                        "tiny.output.dagger.size"));
        assertEquals(properties.get(BenchmarkDataFile.STING_VERSION_KEY), "0.0-test");
        assertEquals(properties.get(BenchmarkDataFile.DAGGER_VERSION_KEY), "2.25.2");
        assertEquals(properties.get(BenchmarkDataFile.GENERATED_BY_KEY), BenchmarkDataFile.GENERATED_BY);
        assertEquals(properties.get("tiny.input.layerCount"), "2");
        assertEquals(properties.get("tiny.input.nodesPerLayer"), "5");
        assertEquals(properties.get("tiny.input.inputsPerNode"), "5");
        assertEquals(properties.get("tiny.input.eagerCount"), "5");
        assertEquals(properties.get("tiny.output.sting.size"), "1234");
        assertEquals(properties.get("tiny.output.dagger.size"), "2345");
        assertFalse(properties.containsKey("small.input.layerCount"));
    }

    @Test
    public void archiveOutputIsWrittenOnlyWhenHelperIsCalled() throws Exception {
        final var temporaryDirectory = Files.createTempDirectory("code-size-archive-test-");
        try {
            final var moduleOutput = temporaryDirectory.resolve("module");
            final var extrasOutput = temporaryDirectory.resolve("extras");
            final var sourceDirectory = temporaryDirectory.resolve("src");
            Files.createDirectories(moduleOutput);
            Files.createDirectories(extrasOutput);
            Files.createDirectories(sourceDirectory);
            Files.writeString(moduleOutput.resolve("application.nocache.js"), "js", StandardCharsets.UTF_8);
            Files.writeString(extrasOutput.resolve("compile-report.txt"), "report", StandardCharsets.UTF_8);
            Files.writeString(
                    sourceDirectory.resolve("Application.java"), "class Application {}", StandardCharsets.UTF_8);

            final var archiveRoot = temporaryDirectory.resolve("archive");
            assertFalse(Files.exists(archiveRoot));

            CodeSizeBenchmark.archiveOutputs(
                    archiveRoot, CodeSizeVariant.TINY, "sting", moduleOutput, extrasOutput, sourceDirectory, 42);

            final var archiveDir = archiveRoot.resolve("tiny").resolve("sting");
            assertTrue(Files.exists(archiveDir.resolve("module").resolve("application.nocache.js")));
            assertTrue(Files.exists(archiveDir.resolve("extras").resolve("compile-report.txt")));
            assertTrue(Files.exists(archiveDir.resolve("src").resolve("Application.java")));
            final var statistics = OrderedProperties.read(archiveDir.resolve("statistics.properties"));
            assertEquals(statistics.get("output.sting.size"), "42");
        } finally {
            FileUtil.deleteTreeIfExists(temporaryDirectory);
        }
    }
}
