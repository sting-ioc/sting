package sting.performance.benchmarks;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class OrderedProperties {
    private final LinkedHashMap<String, String> _entries;

    OrderedProperties() {
        _entries = new LinkedHashMap<>();
    }

    private OrderedProperties(final LinkedHashMap<String, String> entries) {
        _entries = entries;
    }

    String get(final String key) {
        return _entries.get(key);
    }

    boolean containsKey(final String key) {
        return _entries.containsKey(key);
    }

    boolean isEmpty() {
        return _entries.isEmpty();
    }

    Set<String> keySet() {
        return Collections.unmodifiableSet(_entries.keySet());
    }

    Map<String, String> asMap() {
        return Collections.unmodifiableMap(_entries);
    }

    void put(final String key, final String value) {
        if (key.isBlank()) {
            throw new IllegalArgumentException("Property key must not be blank");
        }
        if (null != _entries.putIfAbsent(key, value)) {
            throw new IllegalArgumentException("Duplicate property key: " + key);
        }
    }

    void putAll(final OrderedProperties properties) {
        for (final var entry : properties.asMap().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    static OrderedProperties read(final Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    static OrderedProperties read(final BufferedReader reader) throws IOException {
        final var entries = new LinkedHashMap<String, String>();
        String line;
        int lineNumber = 0;
        while (null != (line = reader.readLine())) {
            lineNumber++;
            final var trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("!")) {
                continue;
            }
            final var separator = separatorIndex(line);
            if (-1 == separator) {
                throw new IOException("Invalid properties line " + lineNumber + ": " + line);
            }
            final var key = line.substring(0, separator).trim();
            final var value = line.substring(separator + 1).trim();
            if (key.isEmpty()) {
                throw new IOException("Invalid blank key on line " + lineNumber);
            }
            if (null != entries.putIfAbsent(key, value)) {
                throw new IOException("Duplicate property key on line " + lineNumber + ": " + key);
            }
        }
        return new OrderedProperties(entries);
    }

    void write(final Path path) throws IOException {
        final var parent = path.getParent();
        if (null != parent) {
            Files.createDirectories(parent);
        }
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            write(writer);
        }
    }

    void write(final BufferedWriter writer) throws IOException {
        for (final var entry : _entries.entrySet()) {
            writer.write(entry.getKey());
            writer.write('=');
            writer.write(entry.getValue());
            writer.newLine();
        }
    }

    private static int separatorIndex(final String line) {
        final var equalsIndex = line.indexOf('=');
        final var colonIndex = line.indexOf(':');
        if (-1 == equalsIndex) {
            return colonIndex;
        }
        if (-1 == colonIndex) {
            return equalsIndex;
        }
        return Math.min(equalsIndex, colonIndex);
    }
}
