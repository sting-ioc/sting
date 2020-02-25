package sting.performance;

import dagger.internal.codegen.ComponentProcessor;
import gir.io.Exec;
import gir.io.FileUtil;
import gir.sys.SystemProperty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import sting.processor.StingProcessor;

public class CodeSizePerformanceTest
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
      case "lazy_medium":
        collectStatistics( "lazy_medium", fixtureStatistics, 5, 50, 0.0 );
        break;
      case "eager_medium":
        collectStatistics( "eager_medium", fixtureStatistics, 5, 50, 1.0 );
        break;
      case "large":
        collectStatistics( "large", fixtureStatistics, 5, 100, 0.5 );
        break;
      case "lazy_large":
        collectStatistics( "lazy_large", fixtureStatistics, 5, 100, 0.0 );
        break;
      case "eager_large":
        collectStatistics( "eager_large", fixtureStatistics, 5, 100, 1.0 );
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
      runTestScenario( variant, layerCount, nodesPerLayer, 5, eagerCount );

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
  @Nonnull
  public static OrderedProperties runTestScenario( @Nonnull final String label,
                                                   final int layerCount,
                                                   final int nodesPerLayer,
                                                   final int inputsPerNode,
                                                   final int eagerCount )
    throws Exception
  {
    final OrderedProperties statistics = new OrderedProperties();
    statistics.setProperty( "input.layerCount", String.valueOf( layerCount ) );
    statistics.setProperty( "input.nodesPerLayer", String.valueOf( nodesPerLayer ) );
    statistics.setProperty( "input.inputsPerNode", String.valueOf( inputsPerNode ) );
    statistics.setProperty( "input.eagerCount", String.valueOf( eagerCount ) );
    final Path workingDirectory = TestUtil.getWorkingDirectory();
    FileUtil.deleteDirIfExists( workingDirectory );
    Files.createDirectories( workingDirectory );
    FileUtil.inDirectory( workingDirectory, () -> {
      final Path src = FileUtil.getCurrentDirectory().resolve( "src" );
      performTests( label,
                    false,
                    new Scenario( src, layerCount, nodesPerLayer, inputsPerNode, eagerCount ),
                    statistics );
      performTests( label,
                    true,
                    new Scenario( src, layerCount, nodesPerLayer, inputsPerNode, eagerCount ),
                    statistics );
    } );
    final double ratio =
      ( Long.parseLong( statistics.getProperty( "output.dagger.size" ) ) * 1.0 ) /
      Long.parseLong( statistics.getProperty( "output.sting.size" ) );
    statistics.setProperty( "output.sting2dagger.sizeRatio", String.format( "%.3f", ratio ) );
    return statistics;
  }

  private static void performTests( @Nonnull final String label,
                                    final boolean isDagger,
                                    @Nonnull final Scenario scenario,
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

    final List<String> classnames = new ArrayList<>( scenario.getNodeClassNames() );
    classnames.addAll( scenario.getInjectorClassNames() );
    classnames.addAll( scenario.getEntryClassNames() );
    TestEngine.compile( isDagger ? ComponentProcessor::new : StingProcessor::new,
                        classnames,
                        scenario.getOutputDirectory(),
                        scenario.getOutputDirectory() );

    final String classpath =
      TestEngine.buildClasspath( scenario.getOutputDirectory().toAbsolutePath().toFile() )
        .stream()
        .map( File::toString )
        .collect( Collectors.joining( File.pathSeparator ) );

    // Remove unit cache otherwise GWT can get confused and stall in the optimizer
    FileUtil.deleteDirIfExists( FileUtil.getCurrentDirectory().resolve( "gwt-unitCache" ) );

    // Now perform the GWT compile
    final String moduleName = "com.example.perf." + variant + ".Application";
    Exec.system( "java",
                 "-cp",
                 classpath,
                 "com.google.gwt.dev.Compiler",
                 "-XdisableClassMetadata",
                 "-XdisableCastChecking",
                 "-optimize",
                 "9",
                 "-nocheckAssertions",
                 "-XmethodNameDisplayMode",
                 "NONE",
                 "-noincremental",
                 "-compileReport",
                 moduleName );
    final Path moduleOutput = FileUtil.getCurrentDirectory().resolve( "war" ).resolve( moduleName );
    final Path jsFile = moduleOutput.resolve( moduleName + ".nocache.js" );
    if ( !Files.exists( jsFile ) )
    {
      throw new IllegalStateException( "Expected to find generated js file at " + jsFile );
    }
    final long size = Files.size( jsFile );

    final OrderedProperties properties = new OrderedProperties();
    System.out.println( label + " js file size: " + size );
    results.setProperty( "output." + variant + ".size", String.valueOf( size ) );
    properties.setProperty( "output." + variant + ".size", String.valueOf( size ) );
    final Path archiveDir = getArchivePath().resolve( variant );
    Files.createDirectories( archiveDir );
    FileUtil.copyDirectory( moduleOutput, archiveDir.resolve( moduleOutput.getFileName() ) );
    FileUtil.copyDirectory( FileUtil.getCurrentDirectory().resolve( "extras" ).resolve( moduleName ),
                            archiveDir.resolve( "extras" ) );
    FileUtil.copyDirectory( scenario.getOutputDirectory(), archiveDir.resolve( "src" ) );
    TestUtil.writeProperties( archiveDir.resolve( "statistics.properties" ), properties );
  }

  @Nonnull
  private static Path getFixtureStatisticsPath()
  {
    return TestUtil.getFixtureDirectory().resolve( "code-size.properties" );
  }

  @Nonnull
  private static Path getArchivePath()
  {
    return TestUtil.getWorkingDirectory().resolve( "archive" );
  }
}
