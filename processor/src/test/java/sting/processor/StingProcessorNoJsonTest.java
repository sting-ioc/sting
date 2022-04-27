package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
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
      CompileTestUtil.compile( inputs( classname ),
                               options,
                               Collections.singletonList( new StingProcessor() ),
                               Collections.emptyList() );

    assertCompilationSuccessful( compilation );

    final List<String> jsonFiles =
      compilation
        .classOutputFilenames()
        .stream()
        .filter( name -> name.endsWith( StingProcessor.JSON_SUFFIX ) )
        .toList();

    // Expect that there are no generated json files
    assertEquals( jsonFiles, Collections.<String>emptyList() );
  }
}
