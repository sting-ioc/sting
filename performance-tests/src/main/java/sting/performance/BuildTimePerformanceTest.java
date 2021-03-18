package sting.performance;

import dagger.internal.codegen.ComponentProcessor;
import gir.io.FileUtil;
import gir.sys.SystemProperty;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import sting.processor.StingProcessor;

public class BuildTimePerformanceTest
{
  public static void main( final String[] args )
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

    final String prefix = TestUtil.getVersion() + "." + variant;
    System.out.println();
    System.out.println();
    System.out.println( prefix + " Scenario Statistics" );
    scenarioStatistics.keySet().forEach( k -> System.out.println( k + ": " + scenarioStatistics.get( k ) ) );
    System.out.println();
    System.out.println();
    fixtureStatistics.removeWithPrefix( prefix );
    fixtureStatistics.mergeWithPrefix( scenarioStatistics, prefix + "." );
    final Path path = getFixtureStatisticsPath();
    System.out.println( "Updating fixture statistics at " + path + "." );
    TestUtil.writeProperties( path, fixtureStatistics );
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
  @SuppressWarnings( "SameParameterValue" )
  @Nonnull
  private static OrderedProperties runTestScenario( @Nonnull final String label,
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
    final Path workingDirectory = TestUtil.getWorkingDirectory();
    Files.createDirectories( workingDirectory );
    FileUtil.inDirectory( workingDirectory, () -> {
      final Path src = FileUtil.getCurrentDirectory().resolve( "src" );
      performTests( label,
                    false,
                    new Scenario( src, layerCount, nodesPerLayer, inputsPerNode, eagerCount ),
                    warmupTimeInSeconds,
                    measureTrials,
                    statistics );
      performTests( label,
                    true,
                    new Scenario( src, layerCount, nodesPerLayer, inputsPerNode, eagerCount ),
                    warmupTimeInSeconds,
                    measureTrials,
                    statistics );
    } );
    final double minAllRatio =
      ( Long.parseLong( (String) statistics.remove( "output.dagger.all.min" ) ) * 1.0 ) /
      Long.parseLong( (String) statistics.remove( "output.sting.all.min" ) );
    final double minIncrementalRatio =
      ( Long.parseLong( (String) statistics.remove( "output.dagger.incremental.min" ) ) * 1.0 ) /
      Long.parseLong( (String) statistics.remove( "output.sting.incremental.min" ) );
    statistics.setProperty( "output.sting2dagger.all.min", String.format( "%.3f", minAllRatio ) );
    statistics.setProperty( "output.sting2dagger.incremental.min", String.format( "%.3f", minIncrementalRatio ) );
    return statistics;
  }

  private static void performTests( @Nonnull final String label,
                                    final boolean isDagger,
                                    @Nonnull final Scenario scenario,
                                    final int warmupTimeInSeconds,
                                    final int trialCount,
                                    @Nonnull final Properties results )
    throws IOException
  {
    final String variant = isDagger ? "dagger" : "sting";
    if ( isDagger )
    {
      DaggerSourceGenerator.createDaggerInjectScenarioSource( scenario );
    }
    else
    {
      StingSourceGenerator.createStingInjectableScenarioSource( scenario );
    }

    // AllInOne compiles
    {
      final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
      classnames.addAll( scenario.getInjectorClassNames() );
      classnames.addAll( scenario.getEntryClassNames() );
      final long[] durations =
        compileTrials( label + " " + variant + " All",
                       scenario,
                       warmupTimeInSeconds,
                       trialCount,
                       isDagger ? ComponentProcessor::new : StingProcessor::new,
                       classnames );
      Arrays.stream( durations )
        .min()
        .ifPresent( v -> results.setProperty( "output." + variant + ".all.min", String.valueOf( v ) ) );
    }

    // Incremental compiles
    {
      TestEngine.compile( StingProcessor::new,
                          scenario.getNodeClassNames(),
                          scenario.getOutputDirectory(),
                          scenario.getOutputDirectory() );
      final long[] durations =
        compileTrials( label + " " + variant + " Incremental",
                       scenario,
                       warmupTimeInSeconds,
                       trialCount,
                       isDagger ? ComponentProcessor::new : StingProcessor::new,
                       scenario.getInjectorClassNames() );
      Arrays.stream( durations )
        .min()
        .ifPresent( v -> results.setProperty( "output." + variant + ".incremental.min", String.valueOf( v ) ) );
    }
  }

  @Nonnull
  private static Path getFixtureStatisticsPath()
  {
    return TestUtil.getFixtureDirectory().resolve( "build-times.properties" );
  }

  private static long[] compileTrials( @Nonnull final String label,
                                       @Nonnull final Scenario scenario,
                                       final int warmupTimeInSeconds,
                                       final int trialCount,
                                       @Nonnull final Supplier<Processor> processorSupplier,
                                       @Nonnull final List<String> classnames )
    throws IOException
  {
    final long endWarmup = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( warmupTimeInSeconds );
    while ( System.currentTimeMillis() < endWarmup )
    {
      final long duration = compileTrial( scenario, processorSupplier, classnames );
      System.out.println( label + " Warmup Trial duration: " + duration );
    }
    final long[] durations = new long[ trialCount ];
    for ( int i = 0; i < durations.length; i++ )
    {
      final long duration = compileTrial( scenario, processorSupplier, classnames );
      durations[ i ] = duration;
      System.out.println( label + " Trial duration: " + duration );
    }
    return durations;
  }

  private static long compileTrial( @Nonnull final Scenario scenario,
                                    @Nonnull final Supplier<Processor> processorSupplier,
                                    @Nonnull final List<String> classnames )
    throws IOException
  {
    final Path outputDir = FileUtil.createLocalTempDir();
    final long duration = TestEngine.compile( processorSupplier, classnames, scenario.getOutputDirectory(), outputDir );
    FileUtil.deleteDirIfExists( outputDir );
    return duration;
  }
}
