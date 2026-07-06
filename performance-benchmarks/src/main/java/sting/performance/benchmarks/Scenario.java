package sting.performance.benchmarks;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Scenario {
    private final Path _outputDirectory;
    private final ScenarioVariant _variant;
    private final List<String> _nodeClassNames = new ArrayList<>();
    private final List<String> _injectorClassNames = new ArrayList<>();
    private final List<String> _entryClassNames = new ArrayList<>();

    Scenario(final Path outputDirectory, final BuildTimeVariant variant) {
        this(
                outputDirectory,
                variant.layerCount(),
                variant.nodesPerLayer(),
                variant.inputsPerNode(),
                variant.eagerCount());
    }

    Scenario(final Path outputDirectory, final CodeSizeVariant variant) {
        this(
                outputDirectory,
                variant.layerCount(),
                variant.nodesPerLayer(),
                variant.inputsPerNode(),
                variant.eagerCount());
    }

    private Scenario(
            final Path outputDirectory,
            final int layerCount,
            final int nodesPerLayer,
            final int inputsPerNode,
            final int eagerCount) {
        _outputDirectory = outputDirectory;
        _variant = new ScenarioVariant(layerCount, nodesPerLayer, inputsPerNode, eagerCount);
    }

    Path outputDirectory() {
        return _outputDirectory;
    }

    int layerCount() {
        return _variant.layerCount();
    }

    int nodesPerLayer() {
        return _variant.nodesPerLayer();
    }

    int inputsPerNode() {
        return _variant.inputsPerNode();
    }

    int eagerCount() {
        return _variant.eagerCount();
    }

    void addNodeClassName(final String classname) {
        _nodeClassNames.add(classname);
    }

    List<String> nodeClassNames() {
        return Collections.unmodifiableList(_nodeClassNames);
    }

    void addInjectorClassName(final String classname) {
        _injectorClassNames.add(classname);
    }

    List<String> injectorClassNames() {
        return Collections.unmodifiableList(_injectorClassNames);
    }

    void addEntryClassName(final String classname) {
        _entryClassNames.add(classname);
    }

    List<String> entryClassNames() {
        return Collections.unmodifiableList(_entryClassNames);
    }

    private record ScenarioVariant(int layerCount, int nodesPerLayer, int inputsPerNode, int eagerCount) {}
}
