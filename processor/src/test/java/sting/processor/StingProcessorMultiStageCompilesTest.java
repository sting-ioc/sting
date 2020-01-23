package sting.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class StingProcessorMultiStageCompilesTest
  extends AbstractStingProcessorTest
{
  @Test
  public void multiStageSuccessfulCompile()
    throws Exception
  {
    final Compilation stage1 =
      Compiler.javac()
        .withProcessors( Collections.singletonList( processor() ) )
        .withOptions( getOptions() )
        .compile( inputs( "com.example.multistage.stage1.Model1",
                          "com.example.multistage.stage1.Model2",
                          "com.example.multistage.stage1.MyFragment" ) );

    assertCompilationSuccessful( stage1 );
    final ImmutableList<JavaFileObject> stage1Output = stage1.generatedFiles();

    assertEquals( stage1Output.size(), 5 );
    assertClassFileCount( stage1Output, 3L );
    assertDescriptorCount( stage1Output, 2L );

    final Compilation stage2 =
      Compiler.javac()
        .withProcessors( Collections.singletonList( processor() ) )
        .withOptions( getOptions() )
        .compile( inputs( "com.example.multistage.stage2.Model3" ) );

    assertCompilationSuccessful( stage2 );
    final ImmutableList<JavaFileObject> stage2Output = stage2.generatedFiles();

    assertEquals( stage2Output.size(), 2 );
    assertClassFileCount( stage2Output, 1L );
    assertDescriptorCount( stage2Output, 1L );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );
    outputFiles( stage2.generatedFiles(), targetDir );

    final ImmutableList<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage3 =
      Compiler.javac()
        .withProcessors( Collections.singletonList( processor() ) )
        .withOptions( getOptions() )
        .withClasspath( classPath )
        .compile( inputs( "com.example.multistage.stage3.MultiStageInjectorModel" ) );

    assertCompilationSuccessful( stage3 );

    final ImmutableList<JavaFileObject> stage3Output = stage3.generatedFiles();

    assertEquals( stage3Output.size(), 1 );
    assertClassFileCount( stage3Output, 1L );
  }

  private void assertDescriptorCount( @Nonnull final ImmutableList<JavaFileObject> output, final long count )
  {
    assertEquals( output.stream().filter( f -> JavaFileObject.Kind.OTHER == f.getKind() ).count(), count );
  }

  private void assertClassFileCount( @Nonnull final ImmutableList<JavaFileObject> output, final long count )
  {
    assertEquals( output.stream().filter( f -> JavaFileObject.Kind.CLASS == f.getKind() ).count(), count );
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.verify_descriptors=true" );
    return options;
  }
}
