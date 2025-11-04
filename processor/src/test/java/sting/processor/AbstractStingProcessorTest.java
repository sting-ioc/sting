package sting.processor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.realityforge.proton.qa.Compilation;

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
    options.add( "-Asting.verbose_out_of_round.errors=false" );
    return options;
  }

  @Nonnull
  final String javaOutput( @Nonnull final String classname )
  {
    return toFilename( classname, "Sting_", ".java" );
  }

  @SuppressWarnings( "SameParameterValue" )
  @Nonnull
  final String graphvizOutput( @Nonnull final String classname )
  {
    return toFilename( classname, "", StingProcessor.DOT_SUFFIX );
  }

  @Nonnull
  final String jsonGraphOutput( @Nonnull final String classname )
  {
    return toFilename( classname, "", StingProcessor.GRAPH_SUFFIX );
  }

  @Nonnull
  final String jsonOutput( @Nonnull final String classname )
  {
    return toFilename( classname, "", StingProcessor.JSON_SUFFIX );
  }

  @SuppressWarnings( "SameParameterValue" )
  final void assertJsonDescriptorCount( @Nonnull final Compilation compilation, final long count )
  {
    compilation.assertClassOutputFilenameCount( f -> f.endsWith( ".json" ), count );
  }

  final void assertJsonDescriptorFile( @Nonnull final Compilation compilation, @Nonnull final String classname )
  {
    compilation.assertClassOutputFilenamePresent( classname.replace( ".", "/" ) + ".json" );
  }
}
