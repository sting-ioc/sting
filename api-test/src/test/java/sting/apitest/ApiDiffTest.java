package sting.apitest;

import gir.io.Exec;
import gir.sys.SystemProperty;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.JsonArray;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class ApiDiffTest
{
  @Test
  public void compareApi()
    throws Exception
  {
    final boolean storeApiDiff = SystemProperty.get( "sting.api_test.store_api_diff" ).equals( "true" );
    final File reportFile = storeApiDiff ? getFixtureReport() : File.createTempFile( "apidiff", ".json" );
    try
    {
      generateReport( reportFile );
      final JsonArray differences = Json.createReader( new FileInputStream( reportFile ) ).readArray();
      final File fixture = getFixtureReport();
      if ( !fixture.exists() )
      {
        if ( !differences.isEmpty() )
        {
          fail( "Unable to locate test fixture describing expected API changes when there is " + differences.size() +
                " differences detected. Expected fixture file to be located at " + fixture.getAbsolutePath() );
        }
      }
      else if ( storeApiDiff && differences.isEmpty() )
      {
        if ( fixture.exists() )
        {
          System.out.println( "Deleting existing fixture file as no API differences detected. Fixture: " + fixture );
          assertTrue( fixture.delete() );
        }
      }
      else
      {
        final byte[] reportData = Files.readAllBytes( reportFile.toPath() );
        final byte[] expectedData = Files.readAllBytes( fixture.toPath() );
        assertEquals( new String( reportData, StandardCharsets.UTF_8 ),
                      new String( expectedData, StandardCharsets.UTF_8 ) );
      }
    }
    finally
    {
      if ( !storeApiDiff )
      {
        assertTrue( !reportFile.exists() || reportFile.delete() );
      }
    }
  }

  private void generateReport( @Nonnull final File reportFile )
  {
    final String oldApiLabel = "org.realityforge.sting:sting-core:jar:" + SystemProperty.get( "sting.prev.version" );
    final String oldApi = oldApiLabel + "::" + SystemProperty.get( "sting.prev.jar" );
    final String newApiLabel = "org.realityforge.sting:sting-core:jar:" + SystemProperty.get( "sting.next.version" );
    final String newApi = newApiLabel + "::" + SystemProperty.get( "sting.next.jar" );
    Exec.system( "java",
                 "-jar",
                 SystemProperty.get( "sting.revapi.jar" ),
                 "--old-api",
                 oldApi,
                 "--new-api",
                 newApi,
                 "--output-file",
                 reportFile.toString() );
  }

  @Nonnull
  private File getFixtureReport()
  {
    return new File( SystemProperty.get( "sting.api_test.fixture_dir" ),
                     SystemProperty.get( "sting.prev.version" ) + "-" +
                     SystemProperty.get( "sting.next.version" ) + ".json" );
  }
}
