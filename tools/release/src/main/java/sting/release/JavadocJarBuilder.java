package sting.release;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public final class JavadocJarBuilder {
    private static final long STABLE_TIME = 0L;

    private JavadocJarBuilder() {}

    public static void main(final String[] args) throws Exception {
        Path output = null;
        final var sourceJars = new ArrayList<Path>();
        final var classpath = new ArrayList<Path>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--output":
                    output = Path.of(args[++i]);
                    break;
                case "--source-jar":
                    sourceJars.add(Path.of(args[++i]));
                    break;
                case "--classpath":
                    classpath.add(Path.of(args[++i]));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument: " + args[i]);
            }
        }
        if (output == null) {
            throw new IllegalArgumentException("Missing --output");
        }
        build(output, sourceJars, classpath);
    }

    private static void build(final Path output, final List<Path> sourceJars, final List<Path> classpath)
            throws IOException, InterruptedException {
        final var work = Files.createTempDirectory("sting-javadoc");
        final var sources = work.resolve("sources");
        final var docs = work.resolve("docs");
        Files.createDirectories(sources);
        Files.createDirectories(docs);
        try {
            for (final Path sourceJar : sourceJars) {
                extractSources(sourceJar, sources);
            }
            final var sourceFiles = collectSourceFiles(sources);
            runJavadoc(docs, sourceFiles, classpath);
            writeJar(output, docs);
        } finally {
            deleteTree(work);
        }
    }

    private static void extractSources(final Path sourceJar, final Path output) throws IOException {
        try (JarFile jar = new JarFile(sourceJar.toFile())) {
            final var entries = Collections.list(jar.entries());
            entries.sort(Comparator.comparing(ZipEntry::getName));
            for (final JarEntry entry : entries) {
                if (!entry.isDirectory() && entry.getName().endsWith(".java")) {
                    final var target = output.resolve(entry.getName());
                    Files.createDirectories(target.getParent());
                    final var content = jar.getInputStream(entry).readAllBytes();
                    if (Files.exists(target)) {
                        final var existing = Files.readAllBytes(target);
                        if (!Arrays.equals(existing, content)) {
                            throw new IOException("Duplicate non-identical source entry: " + entry.getName());
                        }
                    } else {
                        Files.write(target, content);
                    }
                }
            }
        }
    }

    private static List<Path> collectSourceFiles(final Path sources) throws IOException {
        try (Stream<Path> stream = Files.walk(sources)) {
            // Generated Sting_ classes are implementation details, not public API documentation.
            final var files = stream.filter(path -> path.toString().endsWith(".java"))
                    .filter(path -> !path.getFileName().toString().startsWith("Sting_"))
                    .sorted()
                    .collect(Collectors.toList());
            if (files.isEmpty()) {
                throw new IOException("No Java source files found for Javadocs");
            }
            return files;
        }
    }

    private static void runJavadoc(final Path docs, final List<Path> sourceFiles, final List<Path> classpath)
            throws IOException, InterruptedException {
        final var args = new ArrayList<String>();
        args.add("-quiet");
        args.add("-d");
        args.add(docs.toString());
        args.add("-encoding");
        args.add("UTF-8");
        args.add("-charset");
        args.add("UTF-8");
        args.add("-docencoding");
        args.add("UTF-8");
        if (!classpath.isEmpty()) {
            args.add("-classpath");
            args.add(classpath.stream().map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
        }
        sourceFiles.stream().map(Path::toString).forEach(args::add);

        final var javadoc = javaTool("javadoc");
        final var command = new ArrayList<String>();
        command.add(javadoc.toString());
        command.addAll(args);
        final var process = new ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .start();
        final int exit = process.waitFor();
        if (exit != 0) {
            throw new IOException("javadoc exited with status " + exit);
        }
    }

    private static void writeJar(final Path output, final Path docs) throws IOException {
        Files.createDirectories(output.toAbsolutePath().getParent());
        try (JarOutputStream out = new JarOutputStream(Files.newOutputStream(output), new Manifest())) {
            final List<Path> files;
            try (Stream<Path> stream = Files.walk(docs)) {
                files = stream.filter(Files::isRegularFile).sorted().toList();
            }
            for (final Path file : files) {
                final var name = docs.relativize(file).toString().replace('\\', '/');
                final var entry = new JarEntry(name);
                entry.setTime(STABLE_TIME);
                out.putNextEntry(entry);
                Files.copy(file, out);
                out.closeEntry();
            }
        }
    }

    private static Path javaTool(final String name) throws IOException {
        final String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            throw new IOException("java.home system property is not set");
        }
        return Path.of(javaHome, "bin", name);
    }

    private static void deleteTree(final Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        final List<Path> paths;
        try (Stream<Path> stream = Files.walk(root)) {
            paths = stream.sorted(Comparator.reverseOrder()).toList();
        }
        for (final Path path : paths) {
            Files.deleteIfExists(path);
        }
    }
}
