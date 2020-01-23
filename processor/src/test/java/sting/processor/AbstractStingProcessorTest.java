package sting.processor;

import com.google.testing.compile.Compiler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import org.realityforge.proton.qa.AbstractProcessorTest;

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
  protected final Compiler compiler()
  {
    return Compiler.javac()
      .withProcessors( processors() )
      .withOptions( getOptions() );
  }

  @Nonnull
  private List<Processor> processors()
  {
    final List<Processor> processors = new ArrayList<>();
    processors.add( processor() );
    processors.addAll( Arrays.asList( additionalProcessors() ) );
    return processors;
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
}
