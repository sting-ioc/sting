package sting.performance;

import java.io.File;
import java.net.MalformedURLException;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

final class TestEngine
{
  private TestEngine()
  {
  }

  static long compile( @Nonnull final Supplier<Processor> processorSupplier,
                       @Nonnull final List<String> classnames,
                       @Nonnull final Path inputDir,
                       @Nonnull final Path outputDir )
  {
    final List<JavaFileObject> inputs = inputs( inputDir, classnames.toArray( new String[ 0 ] ) );
    final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
    final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    final String classpath =
      buildClasspath( inputDir.toFile() )
        .stream()
        .map( File::toString )
        .collect( Collectors.joining( File.pathSeparator ) );
    final JavaCompiler.CompilationTask task =
      compiler.getTask( null,
                        null,
                        diagnostics,
                        Arrays.asList( "-implicit:none", "-d", outputDir.toString(), "-cp", classpath ),
                        null,
                        inputs );
    task.setProcessors( Collections.singletonList( processorSupplier.get() ) );

    System.gc();
    final long start = System.nanoTime();
    boolean succeeded = task.call();
    final long duration = System.nanoTime() - start;
    System.gc();
    if ( !succeeded )
    {
      throw new AssertionError( describeFailureDiagnostics( diagnostics ) );
    }

    return duration;
  }

  @Nonnull
  private static List<JavaFileObject> inputs( @Nonnull final Path directory, @Nonnull final String... classnames )
  {
    return Stream.of( classnames ).map( classname -> input( directory, classname ) ).collect( Collectors.toList() );
  }

  @Nonnull
  private static JavaFileObject input( @Nonnull final Path directory, @Nonnull final String classname )
  {
    return fixture( directory.resolve( classname.replace( '.', '/' ) + ".java" ) );
  }

  @Nonnull
  private static JavaFileObject fixture( @Nonnull final Path path )
  {
    final StandardJavaFileManager standardFileManager =
      ToolProvider.getSystemJavaCompiler().getStandardFileManager( null, null, null );
    return standardFileManager.getJavaFileObjects( path ).iterator().next();
  }

  /**
   * Returns a description of the why the compilation failed.
   */
  @Nonnull
  private static String describeFailureDiagnostics( @Nonnull final DiagnosticCollector<JavaFileObject> diagnosticCollector )
  {
    final StringBuilder message = new StringBuilder( "Compilation produced the following diagnostics:\n" );
    diagnosticCollector.getDiagnostics().forEach( diagnostic -> message.append( diagnostic ).append( '\n' ) );
    return message.toString();
  }

  /**
   * Build a classpath including the paths specified as well as the current classpath.
   * The current classpath is discovered by inspecting the current classloader, assuming it is a URLClassLoader
   * and walking back to the system classloader adding paths as required.
   *
   * @param paths the additional user supplied paths to add to classpath.
   * @return an list of directories that define the created classpath.
   */
  @Nonnull
  static List<File> buildClasspath( @Nonnull final File... paths )
  {
    final Set<File> elements = new LinkedHashSet<>( Arrays.asList( paths ) );
    ClassLoader classloader = TestEngine.class.getClassLoader();
    while ( true )
    {
      if ( classloader == ClassLoader.getSystemClassLoader() )
      {
        final String[] baseClassPathElements =
          System.getProperty( "java.class.path" ).split( System.getProperty( "path.separator" ) );
        for ( final String element : baseClassPathElements )
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

    return new ArrayList<>( elements );
  }
}
