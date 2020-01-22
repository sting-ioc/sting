package sting.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.tools.Diagnostic;
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
    outputFiles( stage1.generatedFiles(), targetDir, f -> true );
    outputFiles( stage2.generatedFiles(), targetDir, f -> true );

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

  private void assertCompilationSuccessful( @Nonnull final Compilation compilation )
  {
    assertEquals( compilation.status(),
                  Compilation.Status.SUCCESS,
                  compilation.toString() + " - " + describeFailureDiagnostics( compilation ) );
  }

  /**
   * Returns a description of the why the compilation failed.
   */
  @Nonnull
  private String describeFailureDiagnostics( @Nonnull final Compilation compilation )
  {
    final ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics = compilation.diagnostics();
    if ( diagnostics.isEmpty() )
    {
      return "Compilation produced no diagnostics.\n";
    }
    final StringBuilder message = new StringBuilder( "Compilation produced the following diagnostics:\n" );
    diagnostics.forEach( diagnostic -> message.append( diagnostic ).append( '\n' ) );
    return message.toString();
  }

  @Nonnull
  private ImmutableList<File> buildClasspath( @Nonnull final File... paths )
  {
    final Set<File> elements = new LinkedHashSet<>( Arrays.asList( paths ) );
    ClassLoader classloader = getClass().getClassLoader();
    while ( true )
    {
      if ( classloader == ClassLoader.getSystemClassLoader() )
      {
        for ( final String element : System.getProperty( "java.class.path" )
          .split( System.getProperty( "path.separator" ) ) )
        {
          elements.add( new File( element ) );
        }
        break;
      }
      assert classloader instanceof URLClassLoader;
      // We only know how to extract elements from URLClassloaders.
      for ( final URL url : ( (URLClassLoader) classloader ).getURLs() )
      {
        assert url.getProtocol().equals( "file" );
        elements.add( new File( url.getPath() ) );
      }
      classloader = classloader.getParent();
    }

    return elements.stream().collect( ImmutableList.toImmutableList() );
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
