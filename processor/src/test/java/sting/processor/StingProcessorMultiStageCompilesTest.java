package sting.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
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
      compiler()
        .compile( inputs( "com.example.multistage.stage1.Model1",
                          "com.example.multistage.stage1.Model2",
                          "com.example.multistage.stage1.MyFragment" ) );

    assertCompilationSuccessful( stage1 );
    final ImmutableList<JavaFileObject> stage1Output = stage1.generatedFiles();

    assertEquals( stage1Output.size(), 9 );
    assertClassFileCount( stage1Output, 5L );
    assertDescriptorCount( stage1Output, 2L );
    assertSourceFileCount( stage1Output, 2L );

    final Compilation stage2 =
      compiler().compile( inputs( "com.example.multistage.stage2.Model3" ) );

    assertCompilationSuccessful( stage2 );
    final ImmutableList<JavaFileObject> stage2Output = stage2.generatedFiles();

    assertEquals( stage2Output.size(), 4 );
    assertClassFileCount( stage2Output, 2L );
    assertDescriptorCount( stage2Output, 1L );
    assertSourceFileCount( stage2Output, 1L );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( stage1.generatedFiles(), targetDir );
    outputFiles( stage2.generatedFiles(), targetDir );

    final ImmutableList<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage3 =
      compiler()
        .withClasspath( classPath )
        .compile( inputs( "com.example.multistage.stage3.MultiStageInjectorModel" ) );

    assertCompilationSuccessful( stage3 );

    final ImmutableList<JavaFileObject> stage3Output = stage3.generatedFiles();

    assertEquals( stage3Output.size(), 3 );
    assertClassFileCount( stage3Output, 2L );
    assertDescriptorCount( stage3Output, 0L );
    assertSourceFileCount( stage3Output, 1L );
  }
}
