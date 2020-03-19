package sting.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
    final String classname = "com.example.injector.eager.BasicEagerDependencyModel";
    final String objectGraphFilename = jsonGraphOutput( classname );
    final List<String> expectedOutputs = Collections.singletonList( objectGraphFilename );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    assertLazy( nodes, classname + ".MyModel0" );
    assertEager( nodes, classname + ".MyModel1" );
    assertEager( nodes, classname + ".MyModel2" );
    assertLazy( nodes, classname + ".MyModel3" );
    assertEager( nodes, classname + ".MyModel4" );
    assertEager( nodes, classname + ".MyModel5" );
    assertEager( nodes, classname + ".MyModel6" );

    // Order is stable and based on inverse of (depth of node from root dependencies + node id)
    assertIndex( nodes, classname, "MyModel6", 6 );
    assertIndex( nodes, classname, "MyModel5", 5 );
    assertIndex( nodes, classname, "MyModel4", 4 );
    assertIndex( nodes, classname, "MyModel1", 3 );
    assertIndex( nodes, classname, "MyModel3", 2 );
    assertIndex( nodes, classname, "MyModel0", 1 );
    assertIndex( nodes, classname, "MyModel2", 0 );
  }

  @Test
  public void eagerInjectablesAddedWhenAddedViaIncludes()
    throws Exception
  {
    final String pkg = "com.example.injector.eager.fragment";
    final String classname = pkg + ".EagerInjectableViaIncludesModel";
    final String objectGraphFilename = jsonGraphOutput( classname );
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyFragment1",
                                     pkg + ".MyFragment2",
                                     pkg + ".MyModel1",
                                     pkg + ".MyModel2",
                                     pkg + ".MyModel3" ),
                             Collections.singletonList( objectGraphFilename ),
                             t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    // MyModel1 never included
    assertNodeWithIdNotPresent( nodes, pkg + ".MyModel1" );
    // MyModel2 included but is not @Eager and not referenced by a root
    assertNodeWithIdNotPresent( nodes, pkg + ".MyModel2" );
    assertEager( nodes, pkg + ".MyModel3" );
  }

  @SuppressWarnings( "SameParameterValue" )
  private void assertIndex( @Nonnull final JsonArray nodes,
                            @Nonnull final String classname,
                            @Nonnull final String idSuffix,
                            final int index )
  {
    final String expectedId = classname + "." + idSuffix;
    assertTrue( nodes.size() > index,
                "Attempting to lookup " + expectedId + " at index " + index + " but " +
                "there is only " + nodes.size() + " nodes present." );
    final String id = nodes.getJsonObject( index ).getString( "id" );
    assertEquals( id,
                  expectedId,
                  "Attempting to lookup " + expectedId + " at index " + index + " but " +
                  "found id " + id );
  }

  private void assertEager( @Nonnull final JsonArray nodes, @Nonnull final String classname )
  {
    assertTrue( getNodeById( nodes, classname ).getBoolean( "eager", false ) );
  }

  private void assertLazy( @Nonnull final JsonArray nodes, @Nonnull final String classname )
  {
    assertFalse( getNodeById( nodes, classname ).getBoolean( "eager", false ) );
  }

  @Test
  public void recursiveIncludesAreAllIncluded()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.recursive";
    final String classname = pkg + ".RecursiveIncludesModel";
    final String objectGraphFilename = jsonGraphOutput( classname );
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyFragment1",
                                     pkg + ".MyFragment2",
                                     pkg + ".MyFragment3",
                                     pkg + ".MyModel1",
                                     pkg + ".MyModel2",
                                     pkg + ".MyModel3" ),
                             Collections.singletonList( objectGraphFilename ),
                             t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    assertNodeWithIdPresent( nodes, pkg + ".MyFragment1#provideRunnable" );
    assertNodeWithIdPresent( nodes, pkg + ".MyFragment2#provideRunnable" );
    assertNodeWithIdPresent( nodes, pkg + ".MyFragment3#provideRunnable" );
    assertNodeWithIdPresent( nodes, pkg + ".MyModel1" );
    assertNodeWithIdPresent( nodes, pkg + ".MyModel2" );
    assertNodeWithIdPresent( nodes, pkg + ".MyModel3" );
  }

  @Test
  public void compoundNamesInIncludedProviders()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.provider.naming.compound";
    final String classname = pkg + ".MyInjector";
    final String objectGraphFilename = jsonGraphOutput( classname );
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyFrameworkComponent",
                                     pkg + ".MyModel1",
                                     pkg + ".MyModel1Impl",
                                     pkg + ".Outer" ),
                             Collections.singletonList( objectGraphFilename ),
                             t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    assertNodeWithIdPresent( nodes, pkg + ".MyModel1Impl" );
    assertNodeWithIdPresent( nodes, pkg + ".Outer.Middle.Leaf.MyModel2Impl" );
  }

  @Test
  public void enclosingNamesInIncludedProviders()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.provider.naming.enclosing";
    final String classname = pkg + ".MyInjector";
    final String objectGraphFilename = jsonGraphOutput( classname );
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyFrameworkComponent",
                                     pkg + ".MyFramework_MyModel",
                                     pkg + ".MyModel",
                                     pkg + ".Outer" ),
                             Collections.singletonList( objectGraphFilename ),
                             t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    assertNodeWithIdPresent( nodes, pkg + ".MyFramework_MyModel" );
    assertNodeWithIdPresent( nodes, pkg + ".Outer.Middle.Leaf.MyFramework_MyModel2" );
  }

  @Test
  public void flatEnclosingNamesInIncludedProviders()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.provider.naming.flat_enclosing";
    final String classname = pkg + ".MyInjector";
    final String objectGraphFilename = jsonGraphOutput( classname );
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyFrameworkComponent",
                                     pkg + ".MyFramework_MyModel",
                                     pkg + ".MyModel",
                                     pkg + ".Outer",
                                     pkg + ".Outer_Middle_Leaf_MyFramework_MyModel2" ),
                             Collections.singletonList( objectGraphFilename ),
                             t -> emitInjectorGeneratedFile( classname, t ) );
    final JsonArray nodes = readInjectorGraph( objectGraphFilename );
    assertNodeWithIdPresent( nodes, pkg + ".MyFramework_MyModel" );
    assertNodeWithIdPresent( nodes, pkg + ".Outer_Middle_Leaf_MyFramework_MyModel2" );
  }

  private void assertNodeWithIdPresent( @Nonnull final JsonArray nodes,
                                        @Nonnull final String id )
  {
    getNodeById( nodes, id );
  }

  @SuppressWarnings( "SameParameterValue" )
  private void assertNodeWithIdNotPresent( @Nonnull final JsonArray nodes,
                                           @Nonnull final String classname )
  {
    assertNull( findNodeById( nodes, classname ) );
  }

  @Nonnull
  private JsonObject getNodeById( @Nonnull final JsonArray nodes, @Nonnull final String id )
  {
    final JsonObject node = findNodeById( nodes, id );
    assertNotNull( node );
    return node;
  }

  @Nullable
  private JsonObject findNodeById( @Nonnull final JsonArray nodes, @Nonnull final String id )
  {
    return nodes.stream()
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
    return object.getJsonArray( "nodes" );
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

  private boolean emitInjectorGeneratedFile( @Nonnull final String classname, @Nonnull final JavaFileObject target )
  {
    final int index = classname.lastIndexOf( "." );
    final String simpleClassName = -1 == index ? classname : classname.substring( index + 1 );
    return JavaFileObject.Kind.SOURCE == target.getKind() ||
           target.getName().endsWith( simpleClassName + StingProcessor.GRAPH_SUFFIX );
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.emit_json_descriptors=true" );
    return options;
  }
}
