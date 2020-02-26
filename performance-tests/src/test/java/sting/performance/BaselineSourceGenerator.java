package sting.performance;

import com.google.gwt.core.client.EntryPoint;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import elemental2.dom.DomGlobal;
import gir.io.FileUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.lang.model.element.Modifier;

final class BaselineSourceGenerator
{
  private static final String PKG = "com.example.perf.base";

  private BaselineSourceGenerator()
  {
  }

  static void createBaseScenarioSource( @Nonnull final Scenario scenario )
    throws IOException
  {
    final Path outputDirectory = scenario.getOutputDirectory();
    final int layerCount = scenario.getLayerCount();
    final int nodesPerLayer = scenario.getNodesPerLayer();
    final int inputsPerNode = scenario.getInputsPerNode();

    FileUtil.deleteDirIfExists( outputDirectory );

    final ClassName className = ClassName.get( PKG, "ApplicationEntrypoint" );
    final TypeSpec.Builder type =
      TypeSpec
        .classBuilder( className )
        .addModifiers( Modifier.PUBLIC )
        .addSuperinterface( EntryPoint.class );

    for ( int layer = 0; layer < layerCount; layer++ )
    {
      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final String name = nodeMethodName( nodesPerLayer, layer, node );
        type.addField( FieldSpec.builder( ClassName.bestGuess( name ), name, Modifier.STATIC ).build() );

        type.addType( TypeSpec.classBuilder( name ).build() );
      }
    }

    int currentInputNode = 0;
    for ( int layer = 0; layer < layerCount; layer++ )
    {
      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final String methodName = nodeMethodName( nodesPerLayer, layer, node );
        final MethodSpec.Builder compute =
          MethodSpec.methodBuilder( methodName )
            .addModifiers( Modifier.PRIVATE, Modifier.STATIC )
            .addAnnotation( DoNotInline.class );
        if ( 0 != layer )
        {
          for ( int input = 0; input < inputsPerNode; input++ )
          {
            final String name =
              nodeMethodName( nodesPerLayer, layer - 1, ( currentInputNode + input ) % nodesPerLayer );
            compute.addStatement( "$N()", name );
            compute.addStatement( "$T.console.log( $N.hashCode() )", DomGlobal.class, name );
          }
          currentInputNode = ( currentInputNode + inputsPerNode ) % nodesPerLayer;
        }
        type.addMethod( compute.build() );
      }
    }
    final MethodSpec.Builder method =
      MethodSpec
        .methodBuilder( "onModuleLoad" )
        .addModifiers( Modifier.PUBLIC )
        .addAnnotation( Override.class );
    for ( int layer = 0; layer < layerCount; layer++ )
    {
      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final String name = nodeMethodName( nodesPerLayer, layer, node );
        method.addStatement( "$N = new $T()", name, ClassName.bestGuess( name ) );
      }
    }

    for ( int node = 0; node < nodesPerLayer; node++ )
    {
      final String methodName = nodeMethodName( nodesPerLayer, layerCount - 1, node );
      method.addStatement( "$N()", methodName );
    }

    type.addMethod( method.build() );

    JavaFile.builder( className.packageName(), type.build() ).
      skipJavaLangImports( true ).
      build().
      writeTo( outputDirectory, StandardCharsets.UTF_8 );
    scenario.addEntryClassName( className.canonicalName() );
    final String moduleXml =
      "<module>\n" +
      "  <inherits name='com.google.gwt.core.Core'/>\n" +
      "  <inherits name='elemental2.dom.Dom'/>\n" +
      "\n" +
      "  <set-property name='jre.checks.checkLevel' value='MINIMAL'/>\n" +
      "  <set-property name='compiler.stackMode' value='strip'/>\n" +
      "\n" +
      "  <entry-point class='com.example.perf.base.ApplicationEntrypoint'/>\n" +
      "\n" +
      "  <source path=''/>\n" +
      "\n" +
      "  <add-linker name='sso'/>\n" +
      "</module>\n";
    Files.write( outputDirectory.resolve( PKG.replace( '.', '/' ) ).resolve( "Application.gwt.xml" ),
                 moduleXml.getBytes( StandardCharsets.UTF_8 ) );
  }

  @Nonnull
  private static String nodeMethodName( final int nodesPerLayer, final int layer, final int node )
  {
    final int nodeIndex = ( layer * nodesPerLayer ) + node + 1;
    return "Node" + nodeIndex;
  }
}
