package sting.performance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

final class Scenario
{
  @Nonnull
  private final Path _outputDirectory;
  private final int _warmupTrials;
  private final int _measureTrials;
  private final int _layerCount;
  private final int _nodesPerLayer;
  private final int _inputsPerNode;
  private final int _eagerCount;
  @Nonnull
  private final List<String> _nodeClassNames = new ArrayList<>();
  @Nonnull
  private final List<String> _injectorClassNames = new ArrayList<>();
  @Nonnull
  private final List<String> _entryClassNames = new ArrayList<>();
  private long[] _allInOneDurations;
  private long[] _incrementalDurations;

  Scenario( @Nonnull final Path outputDirectory,
            final int warmupTrials,
            final int measureTrials,
            final int layerCount,
            final int nodesPerLayer,
            final int inputsPerNode,
            final int eagerCount )
  {
    _outputDirectory = outputDirectory;
    _warmupTrials = warmupTrials;
    _measureTrials = measureTrials;
    _layerCount = layerCount;
    _nodesPerLayer = nodesPerLayer;
    _inputsPerNode = inputsPerNode;
    _eagerCount = eagerCount;
  }

  @Nonnull
  Path getOutputDirectory()
  {
    return _outputDirectory;
  }

  int getWarmupTrials()
  {
    return _warmupTrials;
  }

  int getMeasureTrials()
  {
    return _measureTrials;
  }

  int getLayerCount()
  {
    return _layerCount;
  }

  int getNodesPerLayer()
  {
    return _nodesPerLayer;
  }

  int getInputsPerNode()
  {
    return _inputsPerNode;
  }

  int getEagerCount()
  {
    return _eagerCount;
  }

  void addNodeClassName( @Nonnull final String classname )
  {
    _nodeClassNames.add( classname );
  }

  @Nonnull
  List<String> getNodeClassNames()
  {
    return Collections.unmodifiableList( _nodeClassNames );
  }

  void addInjectorClassName( @Nonnull final String classname )
  {
    _injectorClassNames.add( classname );
  }

  @Nonnull
  List<String> getInjectorClassNames()
  {
    return Collections.unmodifiableList( _injectorClassNames );
  }

  void addEntryClassName( @Nonnull final String classname )
  {
    _entryClassNames.add( classname );
  }

  @Nonnull
  List<String> getEntryClassNames()
  {
    return Collections.unmodifiableList( _entryClassNames );
  }

  long[] getAllInOneDurations()
  {
    return _allInOneDurations;
  }

  void setAllInOneDurations( final long[] allInOneDurations )
  {
    _allInOneDurations = allInOneDurations;
  }

  long[] getIncrementalDurations()
  {
    return _incrementalDurations;
  }

  void setIncrementalDurations( final long[] incrementalDurations )
  {
    _incrementalDurations = incrementalDurations;
  }
}
