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
      Arrays.asList( toFilename( "expected", classname, "", ".sting.json" ), objectGraphFilename );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray values = readInjectorGraph( objectGraphFilename );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel0", false );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel1", true );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel2", true );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel3", false );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel4", true );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel5", true );
    assertEager( values, "com.example.injector.dependency.eager.BasicEagerDependencyModel.MyModel6", true );
  }

  private void assertEager( @Nonnull final JsonArray values, @Nonnull final String id, final boolean eager )
  {
    final JsonObject value = findValueById( values, id );
    assertNotNull( value );
    assertEquals( value.getBoolean( "eager", false ), eager );
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

  private JsonArray readInjectorGraph( final String filename )
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
    return toFilename( "expected", classname, "", "__ObjectGraph.sting.json" );
  }

  private boolean emitInjectorGeneratedFile( @Nonnull final String classname, @Nonnull final JavaFileObject target )
  {
    final int index = classname.lastIndexOf( "." );
    final String simpleClassName = -1 == index ? classname : classname.substring( index + 1 );
    return JavaFileObject.Kind.SOURCE == target.getKind() ||
           target.getName().endsWith( simpleClassName + ".sting.json" ) ||
           target.getName().endsWith( simpleClassName + "__ObjectGraph.sting.json" );
  }
}
