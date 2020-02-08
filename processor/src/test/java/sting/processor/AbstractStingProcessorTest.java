package sting.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import static org.testng.Assert.*;

public abstract class AbstractStingProcessorTest
  extends AbstractProcessorTest
{
  @Nonnull
  @Override
  protected Processor processor()
  {
    return new StingProcessor();
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "sting";
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.verify_descriptors=true" );
    options.add( "-Asting.verbose_out_of_round.errors=false" );
    return options;
  }

  @Nonnull
  final String javaOutput( @Nonnull final String classname )
  {
    return toFilename( "expected", classname, "Sting_", ".java" );
  }

  @Nonnull
  final String jsonGraphOutput( @Nonnull final String classname )
  {
    return toFilename( "expected", classname, "", StingProcessor.GRAPH_SUFFIX );
  }

  @Nonnull
  final String jsonOutput( @Nonnull final String classname )
  {
    return toFilename( "expected", classname, "", StingProcessor.JSON_SUFFIX );
  }

  //TODO: Move to proton
  final void assertDiagnosticPresent( @Nonnull final Compilation compilation, @Nonnull final String message )
  {
    for ( final Diagnostic<? extends JavaFileObject> diagnostic : compilation.diagnostics() )
    {
      if ( diagnostic.getMessage( Locale.getDefault() ).contains( message ) )
      {
        return;
      }
    }
    fail( "Failed but missing expected message:\n" + message +
          "\nActual diagnostics:\n" + describeFailureDiagnostics( compilation ) );
  }

  final void assertDescriptorCount( @Nonnull final ImmutableList<JavaFileObject> output, final long count )
  {
    assertEquals( output.stream().filter( f -> JavaFileObject.Kind.OTHER == f.getKind() ).count(), count );
  }

  final void assertSourceFileCount( @Nonnull final ImmutableList<JavaFileObject> output, final long count )
  {
    assertEquals( output.stream().filter( f -> JavaFileObject.Kind.SOURCE == f.getKind() ).count(), count );
  }

  final void assertClassFileCount( @Nonnull final ImmutableList<JavaFileObject> output, final long count )
  {
    assertEquals( output.stream().filter( f -> JavaFileObject.Kind.CLASS == f.getKind() ).count(), count );
  }
}
