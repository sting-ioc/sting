package sting.processor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.Test;

public final class StingProcessorMultiStageCompilesTest
  extends AbstractStingProcessorTest
{
  @Test
  public void multiStageSuccessfulCompile()
    throws Exception
  {
    final Compilation stage1 =
      compile( inputs( "com.example.multistage.stage1.Model1",
                       "com.example.multistage.stage1.Model2",
                       "com.example.multistage.stage1.MyFragment" ) );

    assertCompilationSuccessful( stage1 );

    stage1.assertClassFileCount( 5L );
    assertBinaryDescriptorCount( stage1, 2L );
    stage1.assertJavaFileCount( 2L );

    final Compilation stage2 =
      compile( inputs( "com.example.multistage.stage2.Model3" ) );

    assertCompilationSuccessful( stage2 );

    stage2.assertClassFileCount( 2L );
    assertBinaryDescriptorCount( stage2, 1L );
    stage2.assertJavaFileCount( 1L );

    final Path targetDir = Files.createTempDirectory( "sting" );
    CompileTestUtil.outputFiles( stage1, targetDir );
    CompileTestUtil.outputFiles( stage2, targetDir );

    final List<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage3 =
      compile( inputs( "com.example.multistage.stage3.MultiStageInjectorModel" ), classPath );

    assertCompilationSuccessful( stage3 );

    stage3.assertClassFileCount( 2L );
    assertBinaryDescriptorCount( stage3, 0L );
    stage3.assertJavaFileCount( 1L );
  }
}
