package sting.processor;

import com.google.testing.compile.Compilation;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class StingProcessorBadDescriptorTest
  extends AbstractStingProcessorTest
{
  @Test
  public void incorrectMagicValue()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario1.Model1" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario1" )
        .resolve( "Model1.sbf" );

    final RandomAccessFile file = new RandomAccessFile( descriptor.toFile().getAbsolutePath(), "rw" );

    // This is incorrect header
    file.seek( 0 );
    file.writeInt( 0x666 );
    file.close();

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario1.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario1.Model1. Error: java.io.IOException: Descriptor for com.example.bad_descriptors.scenario1.Model1 is in an incorrect format. Bad header." );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void incorrectVersion()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario1.Model1" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario1" )
        .resolve( "Model1.sbf" );

    final RandomAccessFile file = new RandomAccessFile( descriptor.toFile().getAbsolutePath(), "rw" );

    // This is incorrect header
    file.seek( 5 );
    file.writeByte( 0xFF );
    file.close();

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario1.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario1.Model1. Error: java.io.IOException: Descriptor for com.example.bad_descriptors.scenario1.Model1 is in an unknown version: 255" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void malformed()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario1.Model1" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario1" )
        .resolve( "Model1.sbf" );

    final RandomAccessFile file = new RandomAccessFile( descriptor.toFile().getAbsolutePath(), "rw" );

    // This is incorrect header
    file.seek( 8 );
    file.writeInt( 0xFFFFFFFF );
    file.close();

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario1.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario1.Model1. Error: java.io.EOFException" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void truncated()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario1.Model1" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario1" )
        .resolve( "Model1.sbf" );

    truncateDescriptor( descriptor );

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario1.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario1.Model1. Error: java.io.EOFException" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void missingDescriptorForInjectorDependency()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario1.Model1" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario1" )
        .resolve( "Model1.sbf" );

    Files.delete( descriptor );
    assertFalse( Files.exists( descriptor ) );

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario1.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "@Injector target must not contain a non-optional dependency [com.example.bad_descriptors.scenario1.Model1] that can not be satisfied.\n" +
                           "  Dependency Path:\n" +
                           "    [Injector]       com.example.bad_descriptors.scenario1.MyInjectorModel" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void missingDescriptorForInjectableDependency()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario2.Model1",
                                  "com.example.bad_descriptors.scenario2.Model2" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario2" )
        .resolve( "Model1.sbf" );

    truncateDescriptor( descriptor );

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario2.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario2.Model1. Error: java.io.EOFException" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void missingDescriptorForFragmentDependency()
    throws Exception
  {
    final Compilation stage1 =
      compiler().compile( inputs( "com.example.bad_descriptors.scenario3.Model1",
                                  "com.example.bad_descriptors.scenario3.Model2",
                                  "com.example.bad_descriptors.scenario3.MyFragment" ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );

    final Path descriptor =
      targetDir.resolve( "com" )
        .resolve( "example" )
        .resolve( "bad_descriptors" )
        .resolve( "scenario3" )
        .resolve( "Model1.sbf" );

    truncateDescriptor( descriptor );

    final Compilation stage2 =
      compiler()
        .withClasspath( buildClasspath( targetDir.toFile() ) )
        .compile( inputs( "com.example.bad_descriptors.scenario3.MyInjectorModel" ) );

    assertEquals( stage2.status(), Compilation.Status.FAILURE );

    assertErrorDiagnostic( stage2,
                           "Failed to read the Sting descriptor for the type com.example.bad_descriptors.scenario3.Model1. Error: java.io.EOFException" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  private void truncateDescriptor( @Nonnull final Path descriptor )
    throws IOException
  {
    final RandomAccessFile file = new RandomAccessFile( descriptor.toFile().getAbsolutePath(), "rw" );
    file.setLength( 8 );
    file.close();
  }
}
