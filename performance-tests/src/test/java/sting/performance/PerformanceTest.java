package sting.performance;

import dagger.internal.codegen.ComponentProcessor;
import gir.GirException;
import gir.delta.Patch;
import gir.io.FileUtil;
import gir.sys.SystemProperty;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import sting.processor.StingProcessor;
import static org.testng.Assert.*;

public class PerformanceTest
{
  public static void main( String[] args )
    throws Exception
  {
    final OrderedProperties fixtureStatistics = OrderedProperties.load( getFixtureStatisticsPath() );
    final String variant = SystemProperty.get( "sting.perf.variant" );
    switch ( variant )
    {
      case "tiny":
        collectStatistics( "tiny", fixtureStatistics, 2, 5, 0.5 );
        break;
      case "small":
        collectStatistics( "small", fixtureStatistics, 5, 10, 0.5 );
        break;
      case "medium":
        collectStatistics( "medium", fixtureStatistics, 5, 50, 0.5 );
        break;
      case "large":
        collectStatistics( "large", fixtureStatistics, 5, 100, 0.5 );
        break;
      case "huge":
        collectStatistics( "huge", fixtureStatistics, 10, 100, 0.5 );
        break;
      default:
        System.out.println( "Unknown variant: " + variant );
        break;
    }
  }

  private static void updateFixtureStatistics( @Nonnull final OrderedProperties fixtureStatistics )
  {
    final Path path = getFixtureStatisticsPath();
    System.out.println( "Updating fixture statistics at " + path + "." );
    writeProperties( path, fixtureStatistics );
  }

  static void writeProperties( @Nonnull final Path outputFile, @Nonnull final OrderedProperties properties )
  {
    try
    {
      properties.store( new FileWriter( outputFile.toFile() ), "" );
      Patch.file( outputFile, c -> c.replaceAll( "#.*\n", "" ) );
    }
    catch ( final IOException ioe )
    {
      final String message = "Failed to write properties file: " + outputFile;
      System.out.println( message );
      throw new GirException( message, ioe );
    }
  }

  @SuppressWarnings( "SameParameterValue" )
  private static void collectStatistics( @Nonnull final String variant,
                                         @Nonnull final OrderedProperties fixtureStatistics,
                                         final int layerCount,
                                         final int nodesPerLayer,
                                         final double eagerCountRatio )
    throws Exception
  {
    final int eagerCount = (int) ( ( layerCount * nodesPerLayer ) * eagerCountRatio );
    final OrderedProperties scenarioStatistics =
      runTestScenario( variant, 20, 10, layerCount, nodesPerLayer, 5, eagerCount );

    final String prefix = getVersion() + "." + variant;
    System.out.println();
    System.out.println();
    System.out.println( prefix + " Scenario Statistics" );
    scenarioStatistics.keySet().forEach( k -> System.out.println( k + ": " + scenarioStatistics.get( k ) ) );
    System.out.println();
    System.out.println();
    final List<Object> keysToRemove = fixtureStatistics.keySet()
      .stream()
      .filter( k -> ( (String) k ).startsWith( prefix ) )
      .collect( Collectors.toList() );
    keysToRemove.forEach( fixtureStatistics::remove );
    fixtureStatistics.mergeWithPrefix( scenarioStatistics, prefix + "." );
    updateFixtureStatistics( fixtureStatistics );
  }

  @Nonnull
  private static Path getFixtureStatisticsPath()
  {
    return getFixtureDirectory().resolve( "statistics.properties" );
  }

  @Nonnull
  private static String getVersion()
  {
    return SystemProperty.get( "sting.next.version" );
  }

  @Nonnull
  private static Path getFixtureDirectory()
  {
    return Paths.get( SystemProperty.get( "sting.perf.fixture_dir" ) ).toAbsolutePath().normalize();
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
  @Nonnull
  public static OrderedProperties runTestScenario( @Nonnull final String label,
                                                   final int warmupTimeInSeconds,
                                                   final int measureTrials,
                                                   final int layerCount,
                                                   final int nodesPerLayer,
                                                   final int inputsPerNode,
                                                   final int eagerCount )
    throws Exception
  {
    final OrderedProperties statistics = new OrderedProperties();
    statistics.setProperty( "input.warmupTimeInSeconds", String.valueOf( warmupTimeInSeconds ) );
    statistics.setProperty( "input.measureTrials", String.valueOf( measureTrials ) );
    statistics.setProperty( "input.layerCount", String.valueOf( layerCount ) );
    statistics.setProperty( "input.nodesPerLayer", String.valueOf( nodesPerLayer ) );
    statistics.setProperty( "input.inputsPerNode", String.valueOf( inputsPerNode ) );
    statistics.setProperty( "input.eagerCount", String.valueOf( eagerCount ) );
    final Path workingDirectory = getWorkingDirectory();
    Files.createDirectories( workingDirectory );
    FileUtil.inDirectory( workingDirectory, () -> {
      final Scenario stingScenario =
        new Scenario( FileUtil.getCurrentDirectory().resolve( "src" ),
                      warmupTimeInSeconds,
                      measureTrials,
                      layerCount,
                      nodesPerLayer,
                      inputsPerNode,
                      eagerCount );
      performStingTests( label, stingScenario, statistics );
      final Scenario daggerScenario =
        new Scenario( FileUtil.getCurrentDirectory().resolve( "src" ),
                      warmupTimeInSeconds,
                      measureTrials,
                      layerCount,
                      nodesPerLayer,
                      inputsPerNode,
                      eagerCount );
      performDaggerTests( label, daggerScenario, statistics );
    } );
    final double minAllRatio =
      ( Long.parseLong( statistics.getProperty( "output.dagger.all.min" ) ) * 1.0 ) /
      Long.parseLong( statistics.getProperty( "output.sting.all.min" ) );
    final double minIncrementalRatio =
      ( Long.parseLong( statistics.getProperty( "output.dagger.incremental.min" ) ) * 1.0 ) /
      Long.parseLong( statistics.getProperty( "output.sting.incremental.min" ) );
    statistics.setProperty( "output.sting2dagger.all.min", String.format( "%.3f", minAllRatio ) );
    statistics.setProperty( "output.sting2dagger.incremental.min", String.format( "%.3f", minIncrementalRatio ) );
    return statistics;
  }

  private static void performStingTests( @Nonnull final String label,
                                         @Nonnull final Scenario scenario,
                                         @Nonnull final Properties results )
    throws IOException
  {
    StingSourceGenerator.createStingInjectableScenarioSource( scenario );

    // AllInOne compiles
    {
      final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
      classnames.addAll( scenario.getInjectorClassNames() );
      classnames.addAll( scenario.getEntryClassNames() );
      final long[] durations =
        TestEngine.compileTrials( label + " Sting All", scenario, StingProcessor::new, classnames );
      scenario.setAllInOneDurations( durations );
    }

    // Incremental compiles
    {
      TestEngine.compile( StingProcessor::new,
                          scenario.getNodeClassNames(),
                          scenario.getOutputDirectory(),
                          scenario.getOutputDirectory() );
      final long[] durations =
        TestEngine.compileTrials( label + " Sting Incremental",
                                  scenario,
                                  StingProcessor::new,
                                  scenario.getInjectorClassNames() );
      scenario.setIncrementalDurations( durations );
    }
    Arrays.stream( scenario.getAllInOneDurations() )
      .min()
      .ifPresent( v -> results.setProperty( "output.sting.all.min", String.valueOf( v ) ) );
    Arrays.stream( scenario.getAllInOneDurations() )
      .average()
      .ifPresent( v -> results.setProperty( "output.sting.all.average", String.valueOf( (long) v ) ) );
    Arrays.stream( scenario.getIncrementalDurations() )
      .min()
      .ifPresent( v -> results.setProperty( "output.sting.incremental.min", String.valueOf( v ) ) );
    Arrays.stream( scenario.getIncrementalDurations() )
      .average()
      .ifPresent( v -> results.setProperty( "output.sting.incremental.average", String.valueOf( (long) v ) ) );
  }

  private static void performDaggerTests( @Nonnull final String label,
                                          @Nonnull final Scenario scenario,
                                          @Nonnull final Properties results )
    throws IOException
  {
    DaggerSourceGenerator.createDaggerInjectScenarioSource( scenario );

    // AllInOne compiles
    {
      final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
      classnames.addAll( scenario.getInjectorClassNames() );
      classnames.addAll( scenario.getEntryClassNames() );
      final long[] durations =
        TestEngine.compileTrials( label + " Dagger All", scenario, ComponentProcessor::new, classnames );
      scenario.setAllInOneDurations( durations );
    }

    // Incremental compiles
    {
      TestEngine.compile( StingProcessor::new,
                          scenario.getNodeClassNames(),
                          scenario.getOutputDirectory(),
                          scenario.getOutputDirectory() );
      final long[] durations =
        TestEngine.compileTrials( label + " Dagger Incremental",
                                  scenario,
                                  ComponentProcessor::new,
                                  scenario.getInjectorClassNames() );
      scenario.setIncrementalDurations( durations );
    }
    Arrays.stream( scenario.getAllInOneDurations() )
      .min()
      .ifPresent( v -> results.setProperty( "output.dagger.all.min", String.valueOf( v ) ) );
    Arrays.stream( scenario.getAllInOneDurations() )
      .average()
      .ifPresent( v -> results.setProperty( "output.dagger.all.average", String.valueOf( (long) v ) ) );
    Arrays.stream( scenario.getIncrementalDurations() )
      .min()
      .ifPresent( v -> results.setProperty( "output.dagger.incremental.min", String.valueOf( v ) ) );
    Arrays.stream( scenario.getIncrementalDurations() )
      .average()
      .ifPresent( v -> results.setProperty( "output.dagger.incremental.average", String.valueOf( (long) v ) ) );
  }

  @Nonnull
  private static Path getWorkingDirectory()
  {
    final String outputDirectoryName = System.getProperty( "sting.perf.working_directory" );
    assertNotNull( outputDirectoryName );
    return Paths.get( outputDirectoryName );
  }
}
