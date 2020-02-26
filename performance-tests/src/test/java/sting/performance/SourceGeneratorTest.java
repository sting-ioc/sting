package sting.performance;

import gir.sys.SystemProperty;
import java.nio.file.Path;

/**
 * A simple tool that generates source so it can be manually explored
 */
public class SourceGeneratorTest
{
  public static void main( final String[] args )
    throws Exception
  {
    final String variant = SystemProperty.get( "sting.perf.variant" );
    switch ( variant )
    {
      case "tiny":
        generateSource( 2, 5, 0.5 );
        break;
      case "small":
        generateSource( 5, 10, 0.5 );
        break;
      case "medium":
        generateSource( 5, 50, 0.5 );
        break;
      case "large":
        generateSource( 5, 100, 0.5 );
        break;
      case "huge":
        generateSource( 10, 100, 0.5 );
        break;
      default:
        System.out.println( "Unknown variant: " + variant );
        break;
    }
  }

  @SuppressWarnings( "SameParameterValue" )
  private static void generateSource( final int layerCount,
                                      final int nodesPerLayer,
                                      final double eagerCountRatio )
    throws Exception
  {
    final Path workingDirectory = TestUtil.getWorkingDirectory();
    final int eagerCount = (int) ( ( layerCount * nodesPerLayer ) * eagerCountRatio );
    DaggerSourceGenerator.createDaggerInjectScenarioSource( new Scenario( workingDirectory.resolve( "dagger/src" ),
                                                                          layerCount,
                                                                          nodesPerLayer,
                                                                          5,
                                                                          eagerCount ) );
    StingSourceGenerator.createStingInjectableScenarioSource( new Scenario( workingDirectory.resolve( "sting/src" ),
                                                                            layerCount,
                                                                            nodesPerLayer,
                                                                            5,
                                                                            eagerCount ) );
  }
}
