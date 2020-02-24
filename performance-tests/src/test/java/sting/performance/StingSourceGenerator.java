package sting.performance;

import com.google.gwt.core.client.EntryPoint;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
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
import sting.Eager;
import sting.Injectable;
import sting.Injector;

final class StingSourceGenerator
{
  private static final String PKG = "com.example.perf.sting";

  private StingSourceGenerator()
  {
  }

  static void createStingInjectableScenarioSource( @Nonnull final Scenario scenario )
    throws IOException
  {
    final Path outputDirectory = scenario.getOutputDirectory();
    final int layerCount = scenario.getLayerCount();
    final int nodesPerLayer = scenario.getNodesPerLayer();
    final int inputsPerNode = scenario.getInputsPerNode();

    FileUtil.deleteDirIfExists( outputDirectory );

    int currentInputNode = 0;
    int remainingEager = scenario.getEagerCount();
    for ( int layer = 0; layer < layerCount; layer++ )
    {
      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final ClassName className = toInjectableClassName( nodesPerLayer, layer, node );
        final TypeSpec.Builder type =
          TypeSpec
            .classBuilder( className )
            .addModifiers( Modifier.PUBLIC, Modifier.FINAL )
            .addAnnotation( Injectable.class );
        if ( remainingEager > 0 )
        {
          type.addAnnotation( Eager.class );
          remainingEager--;
        }
        final MethodSpec.Builder compute =
          MethodSpec.methodBuilder( "compute" ).addModifiers( Modifier.PUBLIC ).addAnnotation( DoNotInline.class );
        if ( 0 != layer )
        {
          final MethodSpec.Builder constructor = MethodSpec.constructorBuilder();

          for ( int input = 0; input < inputsPerNode; input++ )
          {
            final ClassName inputType =
              toInjectableClassName( nodesPerLayer, layer - 1, ( currentInputNode + input ) % nodesPerLayer );
            final String name = "input" + input;
            type.addField( FieldSpec.builder( inputType, name, Modifier.PRIVATE, Modifier.FINAL ).build() );
            constructor.addParameter( ParameterSpec.builder( inputType, name, Modifier.FINAL ).build() );
            constructor.addStatement( "this.$N = $N", name, name );
            compute.addStatement( "$N.compute()", name );
            compute.addStatement( "$T.console.log( $N )", DomGlobal.class, name );
          }
          type.addMethod( constructor.build() );
          currentInputNode = ( currentInputNode + inputsPerNode ) % nodesPerLayer;
        }
        type.addMethod( compute.build() );

        JavaFile.builder( className.packageName(), type.build() ).
          skipJavaLangImports( true ).
          build().
          writeTo( outputDirectory, StandardCharsets.UTF_8 );
        scenario.addNodeClassName( className.canonicalName() );
      }
    }

    // Create the injector
    {
      final ClassName className = ClassName.get( PKG, "Application" );
      final TypeSpec.Builder type =
        TypeSpec
          .interfaceBuilder( className )
          .addModifiers( Modifier.PUBLIC )
          .addAnnotation( Injector.class );

      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final ClassName inputType = toInjectableClassName( nodesPerLayer, 0, node );
        type.addMethod( MethodSpec.methodBuilder( inputType.simpleName() )
                          .returns( inputType )
                          .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                          .build() );
      }
      JavaFile.builder( className.packageName(), type.build() ).
        skipJavaLangImports( true ).
        build().
        writeTo( outputDirectory, StandardCharsets.UTF_8 );
      scenario.addInjectorClassName( className.canonicalName() );
    }

    // Create the Entrypoint
    {
      final ClassName className = ClassName.get( PKG, "ApplicationEntrypoint" );
      final TypeSpec.Builder type =
        TypeSpec
          .classBuilder( className )
          .addModifiers( Modifier.PUBLIC )
          .addSuperinterface( EntryPoint.class );

      final MethodSpec.Builder method =
        MethodSpec
          .methodBuilder( "onModuleLoad" )
          .addModifiers( Modifier.PUBLIC )
          .addAnnotation( Override.class );
      method.addStatement( "final Application application = new Sting_Application()" );
      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        final ClassName inputType = toInjectableClassName( nodesPerLayer, 0, node );
        method.addStatement( "application.$N().compute()", inputType.simpleName() );
      }

      type.addMethod( method.build() );

      JavaFile.builder( className.packageName(), type.build() ).
        skipJavaLangImports( true ).
        build().
        writeTo( outputDirectory, StandardCharsets.UTF_8 );
      scenario.addEntryClassName( className.canonicalName() );
    }
    final String moduleXml =
      "<module>\n" +
      "  <inherits name='com.google.gwt.core.Core'/>\n" +
      "  <inherits name='elemental2.dom.Dom'/>\n" +
      "  <inherits name='sting.Sting'/>\n" +
      "\n" +
      "  <set-property name='jre.checks.checkLevel' value='MINIMAL'/>\n" +
      "  <set-property name='compiler.stackMode' value='strip'/>\n" +
      "\n" +
      "  <entry-point class='com.example.perf.sting.ApplicationEntrypoint'/>\n" +
      "\n" +
      "  <source path=''/>\n" +
      "\n" +
      "  <add-linker name='sso'/>\n" +
      "</module>\n";
    Files.write( outputDirectory.resolve( PKG.replace( '.', '/' ) ).resolve( "Application.gwt.xml" ),
                 moduleXml.getBytes( StandardCharsets.UTF_8 ) );
  }

  @Nonnull
  private static ClassName toInjectableClassName( final int nodesPerLayer, final int layer, final int node )
  {
    final int nodeIndex = ( layer * nodesPerLayer ) + node + 1;
    return ClassName.get( PKG + ".layer" + ( layer + 1 ), "Node" + nodeIndex );
  }
}
