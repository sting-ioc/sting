package sting.processor;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
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
  final String graphvizOutput( @Nonnull final String classname )
  {
    return toFilename( "expected", classname, "", StingProcessor.DOT_SUFFIX );
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

  final void assertJavaFile( @Nonnull final ImmutableList<JavaFileObject> outputs, @Nonnull final String classname )
  {
    final String filename = "/" + classname.replace( ".", "/" ) + ".java";
    assertTrue( outputs.stream().anyMatch( f -> JavaFileObject.Kind.SOURCE == f.getKind() &&
                                                f.getName().endsWith( filename ) ),
                "Missing java source " + filename );
  }

  final void assertClassFile( @Nonnull final ImmutableList<JavaFileObject> outputs, @Nonnull final String classname )
  {
    final String filename = "/" + classname.replace( ".", "/" ) + ".class";
    assertTrue( outputs.stream().anyMatch( f -> JavaFileObject.Kind.CLASS == f.getKind() &&
                                                f.getName().endsWith( filename ) ),
                "Missing java class " + filename );
  }

  final void assertDescriptorFile( @Nonnull final ImmutableList<JavaFileObject> outputs,
                                   @Nonnull final String classname )
  {
    final String filename = "/" + classname.replace( ".", "/" ) + ".sbf";
    assertTrue( outputs.stream()
                  .anyMatch( f -> JavaFileObject.Kind.OTHER == f.getKind() && f.getName().endsWith( filename ) ),
                "Missing sting descriptor " + filename );
  }
}
