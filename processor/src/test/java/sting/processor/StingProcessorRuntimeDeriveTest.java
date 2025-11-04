package sting.processor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.Test;

public final class StingProcessorRuntimeDeriveTest
  extends AbstractStingProcessorTest
{
  @Test
  public void runtimeDerive_resolves_includes()
    throws Exception
  {
    final Compilation stage1 =
      compile( Arrays.asList( input( "unresolved", "com.example.injector.MyModel" ),
                              input( "unresolved", "com.example.injector.MyFragment" ) ) );

    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting-runtime-derive" );
    CompileTestUtil.outputFiles( stage1.classOutputFilenames(), stage1.classOutput(), targetDir );

    final List<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage2 =
      CompileTestUtil.compile( Collections.singletonList( input( "unresolved",
                                                                 "com.example.injector.UnresolvedElementsInjectorModel" ) ),
                               getOptions(),
                               processors(),
                               classPath );

    assertCompilationSuccessful( stage2 );
  }

  @Test
  public void runtimeDerive_autodiscovery()
    throws Exception
  {
    final Compilation stage1 =
      compile( Collections.singletonList( input( "unresolved", "com.example.autodetect.LibModel" ) ) );
    assertCompilationSuccessful( stage1 );

    final Path targetDir = Files.createTempDirectory( "sting-runtime-derive-autod" );
    CompileTestUtil.outputFiles( stage1.classOutputFilenames(), stage1.classOutput(), targetDir );

    final List<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage2 =
      CompileTestUtil.compile( Collections.singletonList( input( "unresolved",
                                                                 "com.example.autodetect.AppInjector" ) ),
                               getOptions(),
                               processors(),
                               classPath );

    assertCompilationSuccessful( stage2 );
  }
}
