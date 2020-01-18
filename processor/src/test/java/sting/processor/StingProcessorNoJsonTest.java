package sting.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.tools.FileObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public final class StingProcessorNoJsonTest
  extends AbstractStingProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.fragment.BasicModel" },
        new Object[]{ "com.example.injectable.BasicModel" },
        new Object[]{ "com.example.injector.BasicInjectorModel" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.emit_json_descriptors=false" );

    final Compilation compilation =
      Compiler.javac()
        .withProcessors( Collections.singletonList( new StingProcessor() ) )
        .withOptions( options )
        .compile( inputs( classname ) );

    assertEquals( compilation.status(), Compilation.Status.SUCCESS );

    final List<String> jsonFiles =
      compilation.generatedFiles()
        .stream()
        .filter( f -> f.getName().endsWith( StingProcessor.DESCRIPTOR_SUFFIX ) )
        .map( FileObject::getName )
        .collect( Collectors.toList() );

    // Expect that there are no generated json files
    assertEquals( jsonFiles, Collections.<String>emptyList() );
  }
}
