package sting.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.tools.JavaFileObject;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public final class StingProcessorInjectorGraphTest
  extends AbstractStingProcessorTest
{
  @Test
  public void eagerFlagPropagationInInjectors()
    throws Exception
  {
    final String classname = "com.example.injector.dependency.eager.BasicEagerDependencyModel";
    final String objectGraphFilename = toObjectGraphFilename( classname );
    final List<String> expectedOutputs =
      Arrays.asList( toFilename( "expected", classname, "", StingProcessor.DESCRIPTOR_SUFFIX ), objectGraphFilename );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray values = readInjectorGraph( objectGraphFilename );
    assertEager( values, classname, "MyModel0", false );
    assertEager( values, classname, "MyModel1", true );
    assertEager( values, classname, "MyModel2", true );
    assertEager( values, classname, "MyModel3", false );
    assertEager( values, classname, "MyModel4", true );
    assertEager( values, classname, "MyModel5", true );
    assertEager( values, classname, "MyModel6", true );
  }

  @Test
  public void eagerInjectablesAddedWhenAddedViaIncludes()
    throws Exception
  {
    final String classname = "com.example.injector.dependency.eager.EagerInjectableViaIncludesModel";
    final String objectGraphFilename = toObjectGraphFilename( classname );
    final List<String> expectedOutputs =
      Arrays.asList( toFilename( "expected", classname, "", StingProcessor.DESCRIPTOR_SUFFIX ), objectGraphFilename );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray values = readInjectorGraph( objectGraphFilename );
    assertEager( values, classname, "MyModel1", true );
    assertValueWithIdNotPresent( values, classname, "MyModel2" );
    assertEager( values, classname, "MyModel3", true );
  }

  private void assertEager( @Nonnull final JsonArray values,
                            @Nonnull final String classname,
                            @Nonnull final String idSuffix,
                            final boolean eager )
  {
    assertEquals( getValueById( values, classname, idSuffix ).getBoolean( "eager", false ), eager );
  }

  @Test
  public void recursiveIncludesAreAllIncluded()
    throws Exception
  {
    final String classname = "com.example.injector.includes.RecursiveIncludesModel";
    final String objectGraphFilename = toObjectGraphFilename( classname );
    final List<String> expectedOutputs =
      Arrays.asList( toFilename( "expected", classname, "", ".sting.json" ), objectGraphFilename );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray values = readInjectorGraph( objectGraphFilename );
    assertValueWithIdPresent( values, classname, "MyFragment1#provideRunnable" );
    assertValueWithIdPresent( values, classname, "MyFragment2#provideRunnable" );
    assertValueWithIdPresent( values, classname, "MyFragment3#provideRunnable" );
    //TODO: Enable once factories are implemented
    //assertValueWithIdPresent( values, classname, "MyFactory1" );
    //assertValueWithIdPresent( values, classname, "MyFactory2" );
    //assertValueWithIdPresent( values, classname, "MyFactory3" );
    assertValueWithIdPresent( values, classname, "MyModel1" );
    assertValueWithIdPresent( values, classname, "MyModel2" );
    assertValueWithIdPresent( values, classname, "MyModel3" );
  }

  private void assertValueWithIdPresent( @Nonnull final JsonArray values,
                                         @Nonnull final String classname,
                                         @Nonnull final String idSuffix )
  {
    getValueById( values, classname, idSuffix );
  }

  private void assertValueWithIdNotPresent( @Nonnull final JsonArray values,
                                            @Nonnull final String classname,
                                            @Nonnull final String idSuffix )
  {
    assertNull( findValueById( values, classname, idSuffix ) );
  }

  @Nonnull
  private JsonObject getValueById( @Nonnull final JsonArray values,
                                   @Nonnull final String classname,
                                   @Nonnull final String idSuffix )
  {
    return getValueById( values, classname + "." + idSuffix );
  }

  @Nonnull
  private JsonObject getValueById( @Nonnull final JsonArray values, @Nonnull final String id )
  {
    final JsonObject value = findValueById( values, id );
    assertNotNull( value );
    return value;
  }

  @Nullable
  private JsonObject findValueById( @Nonnull final JsonArray values,
                                    @Nonnull final String classname,
                                    @Nonnull final String idSuffix )
  {
    return findValueById( values, classname + "." + idSuffix );
  }

  @Nullable
  private JsonObject findValueById( @Nonnull final JsonArray values, @Nonnull final String id )
  {
    return values.stream()
      .map( v -> (JsonObject) v )
      .filter( v -> v.getString( "id" ).equals( id ) )
      .findAny()
      .orElse( null );
  }

  @Nonnull
  private JsonArray readInjectorGraph( @Nonnull final String filename )
    throws IOException
  {
    final JsonObject object = readJsonObject( fixtureDir().resolve( filename ) );
    assertEquals( object.getString( "schema" ), "graph/1" );
    return object.getJsonArray( "values" );
  }

  @Nonnull
  private JsonObject readJsonObject( @Nonnull final Path path )
    throws IOException
  {
    try ( final FileInputStream inputStream = new FileInputStream( path.toFile() ) )
    {
      try ( final JsonReader parser = Json.createReader( inputStream ) )
      {
        return parser.readObject();
      }
    }
  }

  @Nonnull
  private String toObjectGraphFilename( @Nonnull final String classname )
  {
    return toFilename( "expected", classname, "", StingProcessor.GRAPH_SUFFIX );
  }

  private boolean emitInjectorGeneratedFile( @Nonnull final String classname, @Nonnull final JavaFileObject target )
  {
    final int index = classname.lastIndexOf( "." );
    final String simpleClassName = -1 == index ? classname : classname.substring( index + 1 );
    return JavaFileObject.Kind.SOURCE == target.getKind() ||
           target.getName().endsWith( simpleClassName + StingProcessor.DESCRIPTOR_SUFFIX ) ||
           target.getName().endsWith( simpleClassName + StingProcessor.GRAPH_SUFFIX );
  }
}
