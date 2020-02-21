package sting.performance;

import dagger.internal.codegen.ComponentProcessor;
import gir.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import sting.processor.StingProcessor;
import static org.testng.Assert.*;

public class PerformanceTest
{
  public static void main( String[] args )
    throws Exception
  {
    runTestScenario( 15, 10, 10, 10, 5, ( 10 * 10 ) / 2 );
  }

  /**
   * Run a test scenario.
   *
   * @param layerCount    the number of layers in organization.
   * @param nodesPerLayer the number of nodes per layer.
   * @param inputsPerNode how many inputs does a node consume from previous layer.
   * @param eagerCount    the number of nodes that are considered eager and must be created at startup.
   * @throws Exception if error occurs running scenario
   */
  public static void runTestScenario( final int warmupTrials,
                                      final int measureTrials,
                                      final int layerCount,
                                      final int nodesPerLayer,
                                      final int inputsPerNode,
                                      final int eagerCount )
    throws Exception
  {
    final Path workingDirectory = getWorkingDirectory();
    Files.createDirectories( workingDirectory );
    FileUtil.inDirectory( workingDirectory, () -> {
      final Scenario daggerScenario =
        new Scenario( FileUtil.getCurrentDirectory().resolve( "src" ),
                      warmupTrials,
                      measureTrials,
                      layerCount,
                      nodesPerLayer,
                      inputsPerNode,
                      eagerCount );
      performDaggerTests( daggerScenario );
      final Scenario stingScenario =
        new Scenario( FileUtil.getCurrentDirectory().resolve( "src" ),
                      warmupTrials,
                      measureTrials,
                      layerCount,
                      nodesPerLayer,
                      inputsPerNode,
                      eagerCount );
      performStingTests( stingScenario );
    } );
  }

  private static void performStingTests( @Nonnull final Scenario scenario )
    throws IOException
  {
    StingSourceGenerator.createStingInjectableScenarioSource( scenario );

    // AllInOne compiles
    {
      final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
      classnames.addAll( scenario.getInjectorClassNames() );
      classnames.addAll( scenario.getEntryClassNames() );
      final long[] results = TestEngine.compileTrials( "All-in-one", scenario, StingProcessor::new, classnames );
      scenario.setAllInOneDurations( results );
    }

    // Incremental compiles
    {
      TestEngine.compile( StingProcessor::new,
                          scenario.getNodeClassNames(),
                          scenario.getOutputDirectory(),
                          scenario.getOutputDirectory() );
      final long[] results =
        TestEngine.compileTrials( "Incremental", scenario, StingProcessor::new, scenario.getInjectorClassNames() );
      scenario.setIncrementalDurations( results );
    }
  }

  private static void performDaggerTests( @Nonnull final Scenario scenario )
    throws IOException
  {
    DaggerSourceGenerator.createDaggerInjectScenarioSource( scenario );

    // AllInOne compiles
    {
      final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
      classnames.addAll( scenario.getInjectorClassNames() );
      classnames.addAll( scenario.getEntryClassNames() );
      final long[] results = TestEngine.compileTrials( "All-in-one", scenario, ComponentProcessor::new, classnames );
      scenario.setAllInOneDurations( results );
    }

    // Incremental compiles
    {
      TestEngine.compile( StingProcessor::new,
                          scenario.getNodeClassNames(),
                          scenario.getOutputDirectory(),
                          scenario.getOutputDirectory() );
      final long[] results =
        TestEngine.compileTrials( "Incremental", scenario, ComponentProcessor::new, scenario.getInjectorClassNames() );
      scenario.setIncrementalDurations( results );
    }
  }

  @Nonnull
  private static Path getWorkingDirectory()
  {
    final String outputDirectoryName = System.getProperty( "sting.perf.working_directory" );
    assertNotNull( outputDirectoryName );
    return Paths.get( outputDirectoryName );
  }
}
