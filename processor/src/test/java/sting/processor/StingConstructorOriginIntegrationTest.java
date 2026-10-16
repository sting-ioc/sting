package sting.processor;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipFile;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StingConstructorOriginIntegrationTest {
    @DataProvider(name = "constructors")
    public Object[][] constructors() {
        final var cases = new ArrayList<Object[]>();
        for (final boolean turbine : List.of(false, true)) {
            cases.add(new Object[] {turbine, "@sting.Injectable public class Model {}", false, "Sting_Model"});
            cases.add(new Object[] {
                turbine,
                "public class Model { @sting.Injectable public static class Nested {} }",
                false,
                "Model_Sting_Nested"
            });
            cases.add(new Object[] {
                turbine, "@sting.Injectable public class Model { public Model() {} }", true, "Sting_Model"
            });
            cases.add(new Object[] {
                turbine, "@sting.Injectable public class Model { public Model(String value) {} }", true, "Sting_Model"
            });
            cases.add(new Object[] {
                turbine,
                "public class Model { @sting.Injectable public static class Nested { public Nested() {} } }",
                true,
                "Model_Sting_Nested"
            });
            cases.add(new Object[] {turbine, "@sting.Injectable public class Model { Model() {} }", false, "Sting_Model"
            });
            cases.add(new Object[] {
                turbine,
                "@sting.Injectable public class Model { @SuppressWarnings(\"Sting:PublicConstructor\") public Model()"
                        + " {} }",
                false,
                "Sting_Model"
            });
        }
        return cases.toArray(Object[][]::new);
    }

    @Test(dataProvider = "constructors")
    public void constructorDiagnostics(
            final boolean turbine, final String declaration, final boolean warns, final String generatedType)
            throws Exception {
        final Path directory = Files.createTempDirectory("sting-constructor-origin");
        try {
            final Path source = directory.resolve("Model.java");
            Files.writeString(source, "package samples;\n" + declaration);
            final String classpath = System.getProperty("java.class.path");
            final var command = new ArrayList<>(List.of(
                    Path.of(System.getProperty("java.home"), "bin", "java").toString(), "-cp"));
            if (turbine) {
                command.add(classpath + File.pathSeparator + System.getProperty("sting.turbine.jar"));
                command.addAll(List.of(
                        "com.google.turbine.main.Main",
                        "--output",
                        directory.resolve("header.jar").toString(),
                        "--sources",
                        source.toString(),
                        "--classpath"));
                command.addAll(Arrays.asList(classpath.split(File.pathSeparator)));
                command.addAll(List.of(
                        "--processors",
                        "sting.processor.StingProcessor",
                        "--system",
                        System.getProperty("java.home"),
                        "--javacopts"));
            } else {
                command.add(classpath);
                command.addAll(List.of(
                        "com.sun.tools.javac.Main",
                        "-classpath",
                        classpath,
                        "-processor",
                        "sting.processor.StingProcessor",
                        "-d",
                        directory.toString(),
                        source.toString()));
            }
            command.addAll(List.of("-Asting.warnings_as_errors=true", "-Asting.format_generated_source=false"));
            if (turbine) {
                command.add("--");
            }
            final Process process =
                    new ProcessBuilder(command).redirectErrorStream(true).start();
            final var diagnostics = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(process.waitFor(), warns ? 1 : 0, diagnostics);
            if (warns) {
                assertTrue(
                        diagnostics.contains("@Injectable target should not have a public constructor"), diagnostics);
                assertTrue(diagnostics.contains("Sting:PublicConstructor"), diagnostics);
            } else if (turbine) {
                try (var jar = new ZipFile(directory.resolve("header.jar").toFile())) {
                    assertTrue(
                            jar.getEntry("samples/" + generatedType + ".class") != null,
                            "Sting must generate the factory");
                }
            } else {
                assertTrue(Files.exists(directory.resolve("samples/" + generatedType + ".class")), diagnostics);
            }
        } finally {
            try (var paths = Files.walk(directory)) {
                for (final Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.delete(path);
                }
            }
        }
    }
}
