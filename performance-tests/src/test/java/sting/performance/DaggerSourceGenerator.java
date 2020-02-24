package sting.performance;

import com.google.gwt.core.client.EntryPoint;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import dagger.Component;
import elemental2.dom.DomGlobal;
import gir.io.FileUtil;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.element.Modifier;

final class DaggerSourceGenerator
{
  private static final String PKG = "com.example.perf.dagger";

  private DaggerSourceGenerator()
  {
  }

  static void createDaggerInjectScenarioSource( @Nonnull final Scenario scenario )
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
        final ClassName className = toNodeClassName( nodesPerLayer, layer, node );
        final TypeSpec.Builder type =
          TypeSpec
            .classBuilder( className )
            .addAnnotation( Singleton.class )
            .addModifiers( Modifier.PUBLIC, Modifier.FINAL );
        final MethodSpec.Builder constructor = MethodSpec.constructorBuilder();
        constructor.addAnnotation( Inject.class );
        final MethodSpec.Builder compute =
          MethodSpec.methodBuilder( "compute" ).addModifiers( Modifier.PUBLIC ).addAnnotation( DoNotInline.class );
        if ( 0 != layer )
        {

          for ( int input = 0; input < inputsPerNode; input++ )
          {
            final ClassName inputType =
              toNodeClassName( nodesPerLayer, layer - 1, ( currentInputNode + input ) % nodesPerLayer );
            final String name = "input" + input;
            type.addField( FieldSpec.builder( inputType, name, Modifier.PRIVATE, Modifier.FINAL ).build() );
            constructor.addParameter( ParameterSpec.builder( inputType, name, Modifier.FINAL ).build() );
            constructor.addStatement( "this.$N = $N", name, name );
            compute.addStatement( "$N.compute()", name );
            compute.addStatement( "$T.console.log( $N )", DomGlobal.class, name );
          }
          currentInputNode = ( currentInputNode + inputsPerNode ) % nodesPerLayer;
        }
        type.addMethod( constructor.build() );
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
          .addAnnotation( Singleton.class )
          .addAnnotation( Component.class );

      for ( int node = 0; node < nodesPerLayer; node++ )
      {
        if ( remainingEager > 0 )
        {
          remainingEager--;
        }
        final ClassName inputType = toNodeClassName( nodesPerLayer, 0, node );
        type.addMethod( MethodSpec.methodBuilder( inputType.simpleName() )
                          .returns( inputType )
                          .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                          .build() );
      }

      outer:
      for ( int layer = 1; layer < layerCount; layer++ )
      {
        for ( int node = 0; node < nodesPerLayer; node++ )
        {
          if ( remainingEager > 0 )
          {
            remainingEager--;
            final ClassName inputType = toNodeClassName( nodesPerLayer, layer, node );
            type.addMethod( MethodSpec.methodBuilder( inputType.simpleName() )
                              .returns( inputType )
                              .addModifiers( Modifier.PUBLIC, Modifier.ABSTRACT )
                              .build() );
          }
          else
          {
            break outer;
          }
        }
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
      method.addStatement( "final Application application = DaggerApplication.create()" );

      // This is to make sure the eager ... are eager
      remainingEager = scenario.getEagerCount();
      outer:
      for ( int layer = 0; layer < layerCount; layer++ )
      {
        for ( int node = 0; node < nodesPerLayer; node++ )
        {
          if ( remainingEager > 0 )
          {
            remainingEager--;
            final ClassName inputType = toNodeClassName( nodesPerLayer, layer, node );
            method.addStatement( "final $T $N = application.$N()",
                                 inputType,
                                 inputType.simpleName(),
                                 inputType.simpleName() );
          }
          else
          {
            break outer;
          }
        }
      }

      remainingEager = scenario.getEagerCount();
      outer2:
      for ( int layer = 0; layer < layerCount; layer++ )
      {
        for ( int node = 0; node < nodesPerLayer; node++ )
        {
          if ( remainingEager > 0 )
          {
            remainingEager--;
            final ClassName inputType = toNodeClassName( nodesPerLayer, layer, node );
            method.addStatement( "$N.compute()", inputType.simpleName() );
          }
          else
          {
            break outer2;
          }
        }
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
      "  <inherits name='dagger.Dagger'/>\n" +
      "\n" +
      "  <set-property name='jre.checks.checkLevel' value='MINIMAL'/>\n" +
      "  <set-property name='compiler.stackMode' value='strip'/>\n" +
      "\n" +
      "  <entry-point class='com.example.perf.dagger.ApplicationEntrypoint'/>\n" +
      "\n" +
      "  <source path=''/>\n" +
      "\n" +
      "  <add-linker name='sso'/>\n" +
      "</module>\n";
    Files.write( outputDirectory.resolve( PKG.replace( '.', '/' ) ).resolve( "Application.gwt.xml" ),
                 moduleXml.getBytes( StandardCharsets.UTF_8 ) );
  }

  @Nonnull
  private static ClassName toNodeClassName( final int nodesPerLayer, final int layer, final int node )
  {
    final int nodeIndex = ( layer * nodesPerLayer ) + node + 1;
    return ClassName.get( PKG + ".layer" + ( layer + 1 ), "Node" + nodeIndex );
  }
}
