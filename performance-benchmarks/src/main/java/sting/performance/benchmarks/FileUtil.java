package sting.performance.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

final class FileUtil {
    private FileUtil() {}

    static void deleteTreeIfExists(final Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            final var deletedPaths = paths.sorted(Comparator.reverseOrder()).toList();
            for (final var deletedPath : deletedPaths) {
                Files.delete(deletedPath);
            }
        }
    }

    static void copyTree(final Path source, final Path destination) throws IOException {
        try (var paths = Files.walk(source)) {
            for (final var path : paths.toList()) {
                final var relativePath = source.relativize(path);
                final var target = destination.resolve(relativePath);
                if (Files.isDirectory(path)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        }
    }
}
