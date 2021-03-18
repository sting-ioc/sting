package sting.performance;

import gir.GirException;
import gir.delta.Patch;
import gir.sys.SystemProperty;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import javax.annotation.Nonnull;

final class TestUtil
{
  private TestUtil()
  {
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

  @Nonnull
  static String getVersion()
  {
    return SystemProperty.get( "sting.next.version" );
  }

  @Nonnull
  static Path getFixtureDirectory()
  {
    return Paths.get( SystemProperty.get( "sting.perf.fixture_dir" ) ).toAbsolutePath().normalize();
  }

  @Nonnull
  static Path getWorkingDirectory()
  {
    final String outputDirectoryName = System.getProperty( "sting.perf.working_directory" );
    Objects.requireNonNull( outputDirectoryName );
    return Paths.get( outputDirectoryName ).toAbsolutePath();
  }
}
