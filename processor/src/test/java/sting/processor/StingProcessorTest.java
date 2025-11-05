package sting.processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.realityforge.proton.qa.Compilation;
import org.realityforge.proton.qa.CompileTestUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

@SuppressWarnings( "TextBlockMigration" )
public final class StingProcessorTest
  extends AbstractStingProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.fragment.BasicModel" },
        new Object[]{ "com.example.fragment.MultiProvideModel" },
        new Object[]{ "com.example.fragment.NullableProvidesModel" },

        new Object[]{ "com.example.fragment.dependency.BasicDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.CollectionDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.NullableDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.NonnullDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierCollectionDependencyModel" },

        new Object[]{ "com.example.fragment.eager.EagerModel" },
        new Object[]{ "com.example.fragment.eager.LazyModel" },

        new Object[]{ "com.example.fragment.qualifier.BasicQualifierModel" },
        new Object[]{ "com.example.fragment.qualifier.EmptyQualifierModel" },
        new Object[]{ "com.example.fragment.qualifier.NonStandardQualifierModel" },

        new Object[]{ "com.example.fragment.types.NoTypesModel" },

        new Object[]{ "com.example.injectable.BasicModel" },

        new Object[]{ "com.example.injectable.dependency.BasicDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.CollectionDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.NullableDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.NonnullDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.PackageAccessDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.PublicAccessDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.SupplierCollectionDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.SupplierDependencyModel" },

        new Object[]{ "com.example.injectable.eager.EagerModel" },
        new Object[]{ "com.example.injectable.eager.LazyModel" },

        new Object[]{ "com.example.injectable.qualifier.BasicQualifierModel" },
        new Object[]{ "com.example.injectable.qualifier.EmptyQualifierModel" },
        new Object[]{ "com.example.injectable.qualifier.NonStandardQualifierModel" },

        new Object[]{ "com.example.injectable.types.BasicTypesModel" },
        new Object[]{ "com.example.injectable.types.DefaultTypesModel" },
        new Object[]{ "com.example.injectable.types.NoTypesModel" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname, jsonOutput( classname ) );
  }

  @DataProvider( name = "successfulInjectorCompiles" )
  public Object[][] successfulInjectorCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.injector.BasicInjectorModel" },

        new Object[]{ "com.example.injector.circular.SupplierBrokenChainedCircularDependencyModel" },
        new Object[]{ "com.example.injector.circular.SupplierBrokenDirectCircularDependencyModel" },
        new Object[]{ "com.example.injector.circular.SupplierBrokenFragmentWalkingCircularDependencyModel" },

        new Object[]{ "com.example.injector.gwt.DisableGwtInjectorModel" },
        new Object[]{ "com.example.injector.gwt.EnableGwtInjectorModel" },

        new Object[]{ "com.example.injector.outputs.BasicOutputModel" },
        new Object[]{ "com.example.injector.outputs.CollectionContainingMultipleInstancesOutputModel" },
        new Object[]{ "com.example.injector.outputs.CollectionOutputModel" },
        new Object[]{ "com.example.injector.outputs.ComplexOutputModel" },
        new Object[]{ "com.example.injector.outputs.EmptyCollectionOutputModel" },
        new Object[]{ "com.example.injector.outputs.MultipleOutputModel" },
        new Object[]{ "com.example.injector.outputs.OptionalOutputModel" },
        new Object[]{ "com.example.injector.outputs.OptionalMissingOutputModel" },
        new Object[]{ "com.example.injector.outputs.OptionalProvidesOutputModel" },
        new Object[]{ "com.example.injector.outputs.PrimitiveOutputModel" },
        new Object[]{ "com.example.injector.outputs.QualifiedOutputModel" },
        new Object[]{ "com.example.injector.outputs.SupplierCollectionOutputModel" },
        new Object[]{ "com.example.injector.outputs.SupplierOutputModel" },

        new Object[]{ "com.example.injector.inputs.MultipleInputInjectorModel" },
        new Object[]{ "com.example.injector.inputs.OptionalInputInjectorModel" },
        new Object[]{ "com.example.injector.inputs.PrimitiveInputInjectorModel" },
        new Object[]{ "com.example.injector.inputs.SingleInputInjectorModel" }
      };
  }

  // These tests save less fixtures to the filesystem
  @Test( dataProvider = "successfulInjectorCompiles" )
  public void processSuccessfulInjectorCompile( @Nonnull final String classname )
    throws Exception
  {
    final List<String> expectedOutputs = Arrays.asList( jsonOutput( classname ), jsonGraphOutput( classname ) );
    assertSuccessfulCompile( inputs( classname ), expectedOutputs, t -> emitInjectorGeneratedFile( classname, t ) );
  }

  private boolean emitInjectorGeneratedFile( @Nonnull final String classname, @Nonnull final String target )
  {
    final int index = classname.lastIndexOf( "." );
    final String simpleClassName = -1 == index ? classname : classname.substring( index + 1 );
    return target.endsWith( ".java" ) ||
           target.endsWith( simpleClassName + StingProcessor.JSON_SUFFIX ) ||
           target.endsWith( simpleClassName + StingProcessor.DOT_SUFFIX ) ||
           target.endsWith( simpleClassName + StingProcessor.GRAPH_SUFFIX );
  }

  @DataProvider( name = "successfulAutoFragmentCompiles" )
  public Object[][] successfulAutoFragmentCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.autofragment.BasicAutoFragmentModel",
                      new String[]{ "com.example.autofragment.BasicAutoFragmentFragmentModel",
                                    "com.example.autofragment.BasicAutoFragmentInjectableModel",
                                    "com.example.autofragment.BasicAutoFragmentFrameworkModel",
                                    "com.example.autofragment.BasicAutoFragmentFrameworkModelImpl",
                                    "com.example.autofragment.MyFrameworkComponent" } }
      };
  }

  // These tests save less fixtures to the filesystem
  @Test( dataProvider = "successfulAutoFragmentCompiles" )
  public void processSuccessfulAutoFragmentCompile( @Nonnull final String classname,
                                                    @Nonnull final String... additionalClassnames )
    throws Exception
  {
    final List<String> expectedOutputs =
      Collections.singletonList( toFilename( classname, "Sting_", "_Fragment.java" ) );
    final List<JavaFileObject> javaFileObjects = new ArrayList<>();
    javaFileObjects.addAll( inputs( classname ) );
    javaFileObjects.addAll( inputs( additionalClassnames ) );
    assertSuccessfulCompile( javaFileObjects, expectedOutputs, this::emitAutoFragmentGeneratedFile );

  }

  private boolean emitAutoFragmentGeneratedFile( @Nonnull final String target )
  {
    return target.endsWith( ".java" );
  }

  @Test
  public void nestedInjectable()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.injectable.NestedModel",
                             jsonOutput( "com.example.injectable.NestedModel_MyModel" ) );
  }

  @Test
  public void nestedNestedInjectable()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.injectable.NestedNestedModel",
                             jsonOutput( "com.example.injectable.NestedNestedModel_Middle_MyModel" ) );
  }

  @Test
  public void nestedFragment()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.fragment.NestedModel",
                             jsonOutput( "com.example.fragment.NestedModel_MyModel" ) );
  }

  @Test
  public void nestedNestedFragment()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.fragment.NestedNestedModel",
                             jsonOutput( "com.example.fragment.NestedNestedModel_Middle_MyModel" ) );
  }

  @Test
  public void multipleTypedProviderMethod()
    throws Exception
  {
    final String pkg = "com.example.fragment.types";
    final String classname = pkg + ".BasicTypesModel";
    assertSuccessfulCompile( inputs( classname, pkg + ".MyModel" ),
                             Arrays.asList( javaOutput( classname ), jsonOutput( classname ) ) );
  }

  @Test
  public void packageAccessFragmentDependency()
    throws Exception
  {
    final String pkg = "com.example.fragment.dependency.access.package_access";
    final String classname = pkg + ".PackageAccessDependencyModel";
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyType1",
                                     pkg + ".MyType2",
                                     pkg + ".MyType3",
                                     pkg + ".MyType4",
                                     pkg + ".MyType5" ),
                             Arrays.asList( javaOutput( classname ),
                                            jsonOutput( classname ) ) );
  }

  @Test
  public void publicAccessFragmentDependency()
    throws Exception
  {
    final String pkg = "com.example.fragment.dependency.access.public_access";
    final String classname = pkg + ".PublicAccessDependencyModel";
    assertSuccessfulCompile( inputs( classname,
                                     pkg + ".MyType1",
                                     pkg + ".MyType2",
                                     pkg + ".MyType3",
                                     pkg + ".MyType4",
                                     pkg + ".MyType5" ),
                             Arrays.asList( javaOutput( classname ),
                                            jsonOutput( classname ) ) );
  }

  @Test
  public void basicIncludesFragment()
    throws Exception
  {
    final String classname = "com.example.fragment.includes.BasicIncludesModel";
    assertSuccessfulCompile( inputs( classname, "com.example.fragment.includes.Included1Model" ),
                             Collections.singletonList( jsonOutput( classname ) ) );
  }

  @Test
  public void multipleIncludesFragment()
    throws Exception
  {
    final String classname = "com.example.fragment.includes.MultipleIncludesModel";
    assertSuccessfulCompile( inputs( classname,
                                     "com.example.fragment.includes.Included1Model",
                                     "com.example.fragment.includes.Included2Model" ),
                             Collections.singletonList( jsonOutput( classname ) ) );
  }

  @Test
  public void singleIncludesInjector()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.single";
    assertSuccessfulCompile( inputs( pkg + ".SingleIncludesModel",
                                     pkg + ".MyFragment" ),
                             Arrays.asList( jsonOutput( pkg + ".SingleIncludesModel" ),
                                            javaOutput( pkg + ".SingleIncludesModel" ),
                                            jsonOutput( pkg + ".MyFragment" ),
                                            javaOutput( pkg + ".MyFragment" ) ) );
  }

  @Test
  public void multipleIncludesInjector()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.multiple";
    assertSuccessfulCompile( inputs( pkg + ".MultipleIncludesModel",
                                     pkg + ".MyFragment",
                                     pkg + ".MyModel" ),
                             Arrays.asList( jsonOutput( pkg + ".MultipleIncludesModel" ),
                                            javaOutput( pkg + ".MultipleIncludesModel" ),
                                            jsonOutput( pkg + ".MyFragment" ),
                                            javaOutput( pkg + ".MyFragment" ),
                                            jsonOutput( pkg + ".MyModel" ),
                                            javaOutput( pkg + ".MyModel" ) ) );
  }

  @Test
  public void diamondIncludesInjector()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.diamond";
    assertSuccessfulCompile( inputs( pkg + ".DiamondDependencyIncludesModel",
                                     pkg + ".MyFragment1",
                                     pkg + ".MyFragment2",
                                     pkg + ".MyFragment3",
                                     pkg + ".MyModel" ),
                             Arrays.asList( jsonOutput( pkg + ".DiamondDependencyIncludesModel" ),
                                            javaOutput( pkg + ".DiamondDependencyIncludesModel" ),
                                            jsonOutput( pkg + ".MyFragment1" ),
                                            javaOutput( pkg + ".MyFragment1" ),
                                            jsonOutput( pkg + ".MyFragment2" ),
                                            javaOutput( pkg + ".MyFragment2" ),
                                            jsonOutput( pkg + ".MyFragment3" ),
                                            javaOutput( pkg + ".MyFragment3" ),
                                            jsonOutput( pkg + ".MyModel" ),
                                            javaOutput( pkg + ".MyModel" ) ) );
  }

  @Test
  public void graphvizNameCollision()
    throws Exception
  {
    final String pkg = "com.example.injector.graphviz";
    assertSuccessfulCompile( inputs( pkg + ".NameCollisionInjectorModel",
                                     pkg + ".pkg1.MyModel",
                                     pkg + ".pkg2.MyModel" ),
                             Arrays.asList( jsonOutput( pkg + ".NameCollisionInjectorModel" ),
                                            javaOutput( pkg + ".NameCollisionInjectorModel" ),
                                            graphvizOutput( pkg + ".NameCollisionInjectorModel" ),
                                            jsonOutput( pkg + ".pkg1.MyModel" ),
                                            javaOutput( pkg + ".pkg1.MyModel" ),
                                            jsonOutput( pkg + ".pkg2.MyModel" ),
                                            javaOutput( pkg + ".pkg2.MyModel" ) ) );
  }

  @Test
  public void InjectorIncludesInjector()
    throws Exception
  {
    final String pkg = "com.example.injector.includes.injector";
    assertSuccessfulCompile( inputs( pkg + ".MyInjector",
                                     pkg + ".MyOtherInjectorModel",
                                     pkg + ".MyFragment",
                                     pkg + ".MyModel" ),
                             Arrays.asList( jsonOutput( pkg + ".MyInjector" ),
                                            javaOutput( pkg + ".MyInjector" ),
                                            jsonOutput( pkg + ".MyOtherInjectorModel" ),
                                            javaOutput( pkg + ".MyOtherInjectorModel" ),
                                            jsonOutput( pkg + ".MyFragment" ),
                                            javaOutput( pkg + ".MyFragment" ) ) );
  }

  @Test
  public void todomvcIntegrationTest()
    throws Exception
  {
    final String pkg = "com.example.integration.todomvc";
    assertSuccessfulCompile( inputs( pkg + ".ArezComponent",
                                     pkg + ".ioc.TodoInjector",
                                     pkg + ".model.Arez_BrowserLocation",
                                     pkg + ".model.Arez_TodoRepository",
                                     pkg + ".model.Arez_TodoService",
                                     pkg + ".model.Arez_ViewService",
                                     pkg + ".model.BrowserLocation",
                                     pkg + ".model.BrowserLocationFragment",
                                     pkg + ".model.TodoRepository",
                                     pkg + ".model.TodoService",
                                     pkg + ".model.ViewService" ),
                             Arrays.asList( jsonOutput( pkg + ".ioc.TodoInjector" ),
                                            javaOutput( pkg + ".ioc.TodoInjector" ),
                                            javaOutput( pkg + ".model.Arez_TodoRepository" ),
                                            jsonOutput( pkg + ".model.Arez_TodoRepository" ),
                                            javaOutput( pkg + ".model.Arez_TodoService" ),
                                            jsonOutput( pkg + ".model.Arez_TodoService" ),
                                            javaOutput( pkg + ".model.Arez_ViewService" ),
                                            jsonOutput( pkg + ".model.Arez_ViewService" ),
                                            javaOutput( pkg + ".model.BrowserLocationFragment" ),
                                            jsonOutput( pkg + ".model.BrowserLocationFragment" ) ) );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.autofragment.ContainsAnnotationModel",
                      "@AutoFragment target must not contain any types" },
        new Object[]{ "com.example.autofragment.ClassModel", "@AutoFragment target must be an interface" },
        new Object[]{ "com.example.autofragment.ContainsClassModel",
                      "@AutoFragment target must not contain any types" },
        new Object[]{ "com.example.autofragment.ContainsEnumModel", "@AutoFragment target must not contain any types" },
        new Object[]{ "com.example.autofragment.ContainsFieldModel",
                      "@AutoFragment target must not contain any fields" },
        new Object[]{ "com.example.autofragment.ContainsInterfaceModel",
                      "@AutoFragment target must not contain any types" },
        new Object[]{ "com.example.autofragment.ContainsMethodModel",
                      "@AutoFragment target must not contain any methods" },
        new Object[]{ "com.example.autofragment.EnumModel", "@AutoFragment target must be an interface" },
        new Object[]{ "com.example.autofragment.ExtendsSuperInterfaceModel",
                      "@AutoFragment target must not extend any interfaces" },
        new Object[]{ "com.example.autofragment.ParameterizedModel",
                      "@AutoFragment target must not have type parameters" },
        new Object[]{ "com.example.autofragment.ZeroContributorsModel",
                      "StingProcessor failed to process 1 @AutoFragment annotated types as the fragments either contained no contributors or only had contributors added in the last annotation processor round which is not supported by the @AutoFragment annotation. If the problem is not obvious, consider passing the annotation option sting.debug=true" },
        new Object[]{ "com.example.autofragment.ZeroContributorsModel",
                      "Failed to process the com.example.autofragment.ZeroContributorsModel @AutoFragment as 0 @ContributeTo annotations reference the @AutoFragment" },

        new Object[]{ "com.example.contribute_to.MissingAutoFragmentModel",
                      "Failed to process the @ContributeTo contributors for key 'Foo' as no associated @AutoFragment is on the class path. Impacted contributors included: com.example.contribute_to.MissingAutoFragmentModel" },
        new Object[]{ "com.example.contribute_to.NonCandidateTypeModel",
                      "@ContributeTo target must be annotated with @Injectable, @Fragment or be annotated with an annotation annotated by @ActAsStringComponent or @StingProvider" },

        new Object[]{ "com.example.fragment.ClassModel", "@Fragment target must be an interface" },
        new Object[]{ "com.example.fragment.EnclosedAnnotationFragmentModel",
                      "@Fragment target must not contain any types" },
        new Object[]{ "com.example.fragment.EnclosedClassFragmentModel",
                      "@Fragment target must not contain any types" },
        new Object[]{ "com.example.fragment.EnclosedEnumFragmentModel", "@Fragment target must not contain any types" },
        new Object[]{ "com.example.fragment.EnclosedInterfaceFragmentModel",
                      "@Fragment target must not contain any types" },
        new Object[]{ "com.example.fragment.FragmentExtendsSuperinterfaceModel",
                      "@Fragment target must not extend any interfaces" },
        new Object[]{ "com.example.fragment.Jsr330ScopedFragmentModel",
                      "@Fragment target must not be annotated with an annotation that is annotated with the javax.inject.Scope annotation such as [@javax.inject.Singleton]" },
        new Object[]{ "com.example.fragment.NoProvidesOrIncludesModel",
                      "@Fragment target must contain one or more methods or one or more includes" },
        new Object[]{ "com.example.fragment.ParameterizedModel", "@Fragment target must not have type parameters" },

        new Object[]{ "com.example.fragment.includes.BadTypesInIncludesModel",
                      "@Fragment target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment or @Injectable" },
        new Object[]{ "com.example.fragment.includes.DuplicateIncludesModel",
                      "@Fragment target has an includes parameter containing duplicate includes with the type com.example.fragment.includes.DuplicateIncludesModel.MyComponent" },
        new Object[]{ "com.example.fragment.includes.InvalidProvider1IncludesModel",
                      "@Fragment target has an 'includes' parameter containing the value com.example.fragment.includes.InvalidProvider1IncludesModel.MyComponent that is annotated by @com.example.fragment.includes.InvalidProvider1IncludesModel.StingProvider(name=\"[FlatEnclosingName]MF1_[SimpleName]_Provider\") that is annotated by an invalid @StingProvider annotation missing a 'value' parameter of type string." },
        new Object[]{ "com.example.fragment.includes.InvalidProvider2IncludesModel",
                      "@Fragment target has an 'includes' parameter containing the value com.example.fragment.includes.InvalidProvider2IncludesModel.MyComponent that is annotated by @com.example.fragment.includes.InvalidProvider2IncludesModel.StingProvider(23) that is annotated by an invalid @StingProvider annotation missing a 'value' parameter of type string." },
        new Object[]{ "com.example.fragment.includes.MissingProviderIncludesModel",
                      "@Fragment target has an parameter named 'includes' containing the value com.example.fragment.includes.MissingProviderIncludesModel.MyComponent and that type is annotated by the @StingProvider annotation. The provider annotation expects a provider class named com.example.fragment.includes.MissingProviderIncludesModel_MF1_MyComponent_Provider but no such class exists. The type needs to be removed from the includes or the provider class needs to be present." },
        new Object[]{ "com.example.fragment.includes.MultipleProvidersIncludesModel",
                      "@Fragment target has an 'includes' parameter containing the value com.example.fragment.includes.MultipleProvidersIncludesModel.MyComponent that is annotated by multiple @StingProvider annotations. Matching annotations:\n" +
                      "    com.example.fragment.includes.MultipleProvidersIncludesModel.MyFrameworkComponent1\n" +
                      "    com.example.fragment.includes.MultipleProvidersIncludesModel.MyFrameworkComponent2" },
        new Object[]{ "com.example.fragment.includes.PrimitiveInIncludesModel",
                      "@Fragment target must not include a primitive in the includes parameter" },
        new Object[]{ "com.example.fragment.includes.SelfIncludesModel",
                      "@Fragment target must not include self" },
        new Object[]{ "com.example.fragment.includes.UnannotatedProviderIncludesModel",
                      "@Fragment target has an parameter named 'includes' containing the value com.example.fragment.includes.UnannotatedProviderIncludesModel.MyComponent and that type is annotated by the @StingProvider annotation. The provider annotation expects a provider class named com.example.fragment.includes.UnannotatedProviderIncludesModel.MyComponent_Provider but that class is not annotated with either @Injector or @Fragment" },

        new Object[]{ "com.example.fragment.inputs.ArrayTypeInputModel",
                      "@Fragment target must not contain a method with a parameter that contains an array type" },
        new Object[]{ "com.example.fragment.inputs.NullableCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter annotated with the @Nullable annotation that is not an instance dependency kind" },
        new Object[]{ "com.example.fragment.inputs.NullableSupplierCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter annotated with the @Nullable annotation that is not an instance dependency kind" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedInputModel",
                      "@Fragment target must not contain a method with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedSupplierCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.fragment.inputs.RawCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedSupplierCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.RawSupplierCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.RawSupplierInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a raw type" },
        new Object[]{ "com.example.fragment.inputs.WildcardCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a wildcard type parameter" },
        new Object[]{ "com.example.fragment.inputs.WildcardSupplierCollectionInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a wildcard type parameter" },
        new Object[]{ "com.example.fragment.inputs.WildcardSupplierInputModel",
                      "@Fragment target must not contain a method with a parameter that contains a wildcard type parameter" },

        new Object[]{ "com.example.fragment.provides.AbstractMethodProvidesModel",
                      "@Fragment target must only contain methods with a default modifier" },
        new Object[]{ "com.example.fragment.provides.CdiTypedProvidesModel",
                      "@Fragment target must not contain a method annotated with the javax.enterprise.inject.Typed annotation. Use the sting.Typed annotation instead" },
        new Object[]{ "com.example.fragment.provides.Jsr330ScopedProvidesModel",
                      "@Fragment target must not contain a method that is annotated with an annotation that is annotated with the javax.inject.Scope annotation such as [@javax.inject.Singleton]" },
        new Object[]{ "com.example.fragment.provides.NullablePrimitiveReturnTypeProvidesModel",
                      "@Fragment contains a method that is incorrectly annotated with @Nullable as the return type is a primitive value" },
        new Object[]{ "com.example.fragment.provides.ParameterizedProvidesModel",
                      "@Fragment target must not contain methods with a type parameter" },
        new Object[]{ "com.example.fragment.provides.QualifiedAndNoTypesModel",
                      "@Fragment target must not contain methods that specify zero types with the @Typed annotation and specify a qualifier with the @Named annotation as the qualifier is meaningless" },
        new Object[]{ "com.example.fragment.provides.StaticMethodProvidesModel",
                      "@Fragment target must only contain methods with a default modifier" },
        new Object[]{ "com.example.fragment.provides.VoidReturnTypeProvidesModel",
                      "@Fragment target must only contain methods that return a value" },

        new Object[]{ "com.example.fragment.named.Jsr330NamedInputModel",
                      "@Fragment target must not contain a method with a parameter annotated with the javax.inject.Named annotation. Use the sting.Named annotation instead" },
        new Object[]{ "com.example.fragment.named.Jsr330NamedProvidesModel",
                      "@Fragment target must not contain a method annotated with the javax.inject.Named annotation. Use the sting.Named annotation instead" },

        new Object[]{ "com.example.fragment.provides.types.BadType1Model",
                      "@Typed specified a type that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType2Model",
                      "@Typed specified a type that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType3Model",
                      "@Typed specified a type that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.NoTypesAndLazyModel",
                      "@Fragment target must not contain methods that specify zero types with the @Typed annotation and are not annotated with the @Eager annotation otherwise the component can not be created by the injector" },
        new Object[]{ "com.example.fragment.provides.types.ParameterizedServiceModel",
                      "@Typed specified a type that is a a parameterized type" },

        new Object[]{ "com.example.injectable.AbstractModel", "@Injectable target must not be abstract" },
        new Object[]{ "com.example.injectable.InterfaceModel", "@Injectable target must be a class" },
        new Object[]{ "com.example.injectable.MultipleConstructorModel",
                      "@Injectable target must not have multiple constructors" },
        new Object[]{ "com.example.injectable.NonStaticNestedModel",
                      "@Injectable target must not be a non-static nested class" },
        new Object[]{ "com.example.injectable.ParameterizedModel", "@Injectable target must not have type parameters" },
        new Object[]{ "com.example.injectable.QualifiedWithNoTypesModel",
                      "@Injectable target must not specify zero types with the @Typed annotation and specify a qualifier with the @Named annotation as the qualifier is meaningless" },

        new Object[]{ "com.example.injectable.inputs.ArrayTypeInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains an array type" },
        new Object[]{ "com.example.injectable.inputs.NullableCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter annotated with @Nullable that is not an instance dependency kind" },
        new Object[]{ "com.example.injectable.inputs.NullableSupplierCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter annotated with @Nullable that is not an instance dependency kind" },
        new Object[]{ "com.example.injectable.inputs.ParameterizedCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injectable.inputs.ParameterizedSupplierCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injectable.inputs.ParameterizedInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injectable.inputs.RawCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.RawParameterizedCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.RawParameterizedInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.RawParameterizedSupplierCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.RawSupplierCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.RawSupplierInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a raw type" },
        new Object[]{ "com.example.injectable.inputs.WildcardCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a wildcard type parameter" },
        new Object[]{ "com.example.injectable.inputs.WildcardSupplierCollectionInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a wildcard type parameter" },
        new Object[]{ "com.example.injectable.inputs.WildcardSupplierInputModel",
                      "@Injectable target must not contain a constructor with a parameter that contains a wildcard type parameter" },

        new Object[]{ "com.example.injectable.types.BadType1Model",
                      "@Typed specified a type that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType2Model",
                      "@Typed specified a type that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType3Model",
                      "@Typed specified a type that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.NoTypesAndLazyModel",
                      "@Injectable target must not specify zero types with the @Typed annotation or must be annotated with the @Eager annotation otherwise the component can not be created by the injector" },
        new Object[]{ "com.example.injectable.types.ParameterizedTypeServiceModel",
                      "@Typed specified a type that is a a parameterized type" },

        new Object[]{ "com.example.injector.ClassInjector", "@Injector target must be an interface" },
        new Object[]{ "com.example.injector.DefaultMethodInInjectorModel",
                      "@Injector target must not include default methods" },
        new Object[]{ "com.example.injector.EnclosingAnnotationInjectorModel",
                      "@Injector target must not contain a type that is not annotated by either @Injectable or @Fragment" },
        new Object[]{ "com.example.injector.EnclosingClassInjectorModel",
                      "@Injector target must not contain a type that is not annotated by either @Injectable or @Fragment" },
        new Object[]{ "com.example.injector.EnclosingEnumInjectorModel",
                      "@Injector target must not contain a type that is not annotated by either @Injectable or @Fragment" },
        new Object[]{ "com.example.injector.EnclosingInterfaceInjectorModel",
                      "@Injector target must not contain a type that is not annotated by either @Injectable or @Fragment" },
        new Object[]{ "com.example.injector.EnumInjector", "@Injector target must be an interface" },
        new Object[]{ "com.example.injector.Jsr330ScopedInjectorModel",
                      "@Injector target must not be annotated with an annotation that is annotated with the javax.inject.Scope annotation such as [@javax.inject.Singleton]" },
        new Object[]{ "com.example.injector.MissingPrimitiveDependencyModel",
                      "@Injector target must not contain a non-optional dependency [int] that can not be satisfied.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.MissingPrimitiveDependencyModel\n" +
                      "    [Injectable]     com.example.injector.MissingPrimitiveDependencyModel.MyModel1\n" +
                      "    [Provides]    *  com.example.injector.MissingPrimitiveDependencyModel.MyFragment.provideConfig" },
        new Object[]{ "com.example.injector.MultipleCandidatesForSingularDependencyModel",
                      "@Injector target must not contain a non-collection dependency [java.lang.Runnable] that can be satisfied by multiple nodes.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.MultipleCandidatesForSingularDependencyModel\n" +
                      "  \n" +
                      "  Candidate Nodes:\n" +
                      "    [Provides]       com.example.injector.MultipleCandidatesForSingularDependencyModel.MyFragment1.provideRunnable1\n" +
                      "    [Provides]       com.example.injector.MultipleCandidatesForSingularDependencyModel.MyFragment2.provideRunnable2" },
        new Object[]{ "com.example.injector.NoDirectDependenciesAndNoEagerInIncludesModel",
                      "@Injector target produced an empty object graph. This means that there are no eager nodes in the includes and there are no dependencies or only unsatisfied optional dependencies defined by the injector" },
        new Object[]{ "com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel",
                      "@Injector target must not contain an optional provider method or optional injector input and a non-optional service request for the coordinate [java.lang.Integer]\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel\n" +
                      "    [Injectable]     com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyModel1\n" +
                      "    [Provides]    *  com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment1.provideConfig\n" +
                      "  \n" +
                      "  Bindings:\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment2.provideInteger\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment4.provideInteger" },
        new Object[]{ "com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel",
                      "@Injector target must not contain an optional provider method or optional injector input and a non-optional service request for the coordinate [java.lang.String]\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel\n" +
                      "    [Injectable]     com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyModel1\n" +
                      "    [Provides]    *  com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment1.provideRunnable\n" +
                      "  \n" +
                      "  Binding:\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment2.provideConfig" },
        new Object[]{ "com.example.injector.TypeParametersInjectorModel",
                      "@Injector target must not have type parameters" },
        new Object[]{ "com.example.injector.NamedInjectablesAreNotAutoDiscoverableInjectorModel",
                      "@Injector target must not contain a non-optional dependency [com.example.injector.NamedInjectablesAreNotAutoDiscoverableInjectorModel.MyModel] that can not be satisfied.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.NamedInjectablesAreNotAutoDiscoverableInjectorModel.MyInjector" },
        new Object[]{ "com.example.injector.TypedInjectablesAreNotAutoDiscoverableInjectorModel",
                      "@Injector target must not contain a non-optional dependency [com.example.injector.TypedInjectablesAreNotAutoDiscoverableInjectorModel.MyModel] that can not be satisfied.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.TypedInjectablesAreNotAutoDiscoverableInjectorModel.MyInjector" },

        new Object[]{ "com.example.injector.circular.ChainedCircularDependencyModel",
                      "Injector contains a circular dependency.\n" +
                      "  Path:\n" +
                      "    [Injector]       com.example.injector.circular.ChainedCircularDependencyModel\n" +
                      "    [Injectable] +-< com.example.injector.circular.ChainedCircularDependencyModel.MyModel1\n" +
                      "    [Provides]   |   com.example.injector.circular.ChainedCircularDependencyModel.MyFragment1.provideConfig\n" +
                      "    [Provides]   |   com.example.injector.circular.ChainedCircularDependencyModel.MyFragment2.provideInteger\n" +
                      "    [Injectable] |   com.example.injector.circular.ChainedCircularDependencyModel.MyModel4\n" +
                      "    [Injectable] +-> com.example.injector.circular.ChainedCircularDependencyModel.MyModel1" },
        new Object[]{ "com.example.injector.circular.DirectlyCircularDependencyModel",
                      "Injector contains a circular dependency.\n" +
                      "  Path:\n" +
                      "    [Injector]       com.example.injector.circular.DirectlyCircularDependencyModel\n" +
                      "    [Injectable] +-< com.example.injector.circular.DirectlyCircularDependencyModel.MyModel1\n" +
                      "    [Injectable] |   com.example.injector.circular.DirectlyCircularDependencyModel.MyModel2\n" +
                      "    [Injectable] +-> com.example.injector.circular.DirectlyCircularDependencyModel.MyModel1" },

        new Object[]{ "com.example.injector.includes.BadTypesInIncludesModel",
                      "@Injector target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment or @Injectable" },
        new Object[]{ "com.example.injector.includes.DuplicateIncludesModel",
                      "@Injector target has an includes parameter containing duplicate includes with the type com.example.injector.includes.DuplicateIncludesModel.MyComponent" },
        new Object[]{ "com.example.injector.includes.ExplicitIncludesOfEnclosedFragmentModel",
                      "@Injector target must not include a @Fragment annotated type that is auto-included as it is enclosed within the injector type" },
        new Object[]{ "com.example.injector.includes.ExplicitIncludesOfEnclosedInjectableModel",
                      "@Injector target must not include an @Injectable annotated type that is auto-included as it is enclosed within the injector type" },
        new Object[]{ "com.example.injector.includes.InvalidProvider1IncludesModel",
                      "@Injector target has an 'includes' parameter containing the value com.example.injector.includes.InvalidProvider1IncludesModel.MyComponent that is annotated by @com.example.injector.includes.InvalidProvider1IncludesModel.StingProvider(name=\"[FlatEnclosingName]MF1_[SimpleName]_Provider\") that is annotated by an invalid @StingProvider annotation missing a 'value' parameter of type string." },
        new Object[]{ "com.example.injector.includes.InvalidProvider2IncludesModel",
                      "@Injector target has an 'includes' parameter containing the value com.example.injector.includes.InvalidProvider2IncludesModel.MyComponent that is annotated by @com.example.injector.includes.InvalidProvider2IncludesModel.StingProvider(42) that is annotated by an invalid @StingProvider annotation missing a 'value' parameter of type string." },
        new Object[]{ "com.example.injector.includes.MissingProviderIncludesModel",
                      "@Injector target has an parameter named 'includes' containing the value com.example.injector.includes.MissingProviderIncludesModel_MyComponent and that type is annotated by the @StingProvider annotation. The provider annotation expects a provider class named com.example.injector.includes.MF1_MissingProviderIncludesModel_MyComponent_Provider but no such class exists. The type needs to be removed from the includes or the provider class needs to be present." },
        new Object[]{ "com.example.injector.includes.MultipleProvidersIncludesModel",
                      "@Injector target has an 'includes' parameter containing the value com.example.injector.includes.MultipleProvidersIncludesModel.MyComponent that is annotated by multiple @StingProvider annotations. Matching annotations:\n" +
                      "    com.example.injector.includes.MultipleProvidersIncludesModel.MyFrameworkComponent1\n" +
                      "    com.example.injector.includes.MultipleProvidersIncludesModel.MyFrameworkComponent2" },
        new Object[]{ "com.example.injector.includes.PrimitiveInIncludesModel",
                      "@Injector target must not include a primitive in the includes parameter" },
        new Object[]{ "com.example.injector.includes.SelfIncludesModel",
                      "@Injector target must not include self" },
        new Object[]{ "com.example.injector.includes.UnannotatedProviderIncludesModel",
                      "@Injector target has an parameter named 'includes' containing the value com.example.injector.includes.UnannotatedProviderIncludesModel.MyComponent and that type is annotated by the @StingProvider annotation. The provider annotation expects a provider class named com.example.injector.includes.UnannotatedProviderIncludesModel.MyComponent_Provider but that class is not annotated with either @Injector or @Fragment" },
        new Object[]{ "com.example.injector.includes.UnusedFragmentIncludesModel",
                      "@Injector must not include type com.example.injector.includes.UnusedFragmentIncludesModel.MyFragment when the type is not used within the graph" },
        new Object[]{ "com.example.injector.includes.UnusedInjectableIncludesModel",
                      "@Injector must not include type com.example.injector.includes.UnusedInjectableIncludesModel.MyModel1 when the type is not used within the graph" },
        new Object[]{ "com.example.injector.includes.UnusedStingProviderIncludesModel",
                      "@Injector must not include type com.example.injector.includes.UnusedStingProviderIncludesModel.MyModel1 when the type is not used within the graph" },

        new Object[]{ "com.example.injector.inputs.ArrayTypeInputModel",
                      "@Input must not specify an array type for the type parameter" },
        new Object[]{ "com.example.injector.inputs.MismatchOptionalityInputInjectorModel",
                      "@Injector target must not contain an optional provider method or optional injector input and a non-optional service request for the coordinate [java.lang.Runnable]\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.inputs.MismatchOptionalityInputInjectorModel\n" +
                      "    [Injectable]  *  com.example.injector.inputs.MismatchOptionalityInputInjectorModel.MyModel\n" +
                      "  \n" +
                      "  Binding:\n" +
                      "    [Input]          com.example.injector.inputs.MismatchOptionalityInputInjectorModel.input1/[java.lang.Runnable]?" },
        new Object[]{ "com.example.injector.inputs.RawParameterizedTypeInputModel",
                      "@Input must not specify a parameterized type for the type parameter" },
        new Object[]{ "com.example.injector.inputs.VoidTypeInputModel",
                      "@Input must specify a non-void type for the type parameter" },

        new Object[]{ "com.example.injector.named.Jsr330NamedOutputModel",
                      "@Injector target must not contain a method annotated with the javax.inject.Named annotation. Use the sting.Named annotation instead" },

        new Object[]{ "com.example.injector.outputs.ArrayTypeOutputModel",
                      "@Injector target must not contain a method with a return type that contains an array type" },
        new Object[]{ "com.example.injector.outputs.Jsr330ScopedOutputModel",
                      "@Injector target must not contain a method that is annotated with an annotation that is annotated with the javax.inject.Scope annotation such as [@javax.inject.Singleton]" },
        new Object[]{ "com.example.injector.outputs.NullableCollectionOutputModel",
                      "@Injector target must not contain a method annotated with @Nullable that is not an instance dependency kind" },
        new Object[]{ "com.example.injector.outputs.NullableSupplierCollectionOutputModel",
                      "@Injector target must not contain a method annotated with @Nullable that is not an instance dependency kind" },
        new Object[]{ "com.example.injector.outputs.MethodReturningVoidOutputModel",
                      "@Injector target must not contain a method that has a void return value" },
        new Object[]{ "com.example.injector.outputs.MethodThrowsExceptionOutputModel",
                      "@Injector target must not contain a method that throws any exceptions" },
        new Object[]{ "com.example.injector.outputs.MethodWithParametersOutputModel",
                      "@Injector target must not contain a method that has parameters" },
        new Object[]{ "com.example.injector.outputs.MethodWithTypeParametersOutputModel",
                      "@Injector target must not contain a method that has any type parameters" },
        new Object[]{ "com.example.injector.outputs.MissingOutputModel",
                      "@Injector target must not contain a non-optional dependency [java.lang.Integer] that can not be satisfied.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.outputs.MissingOutputModel\n" +
                      "    [Injectable]     com.example.injector.outputs.MissingOutputModel.MyModel1\n" +
                      "    [Provides]       com.example.injector.outputs.MissingOutputModel.MyFragment1.provideRunnable\n" +
                      "    [Provides]    *  com.example.injector.outputs.MissingOutputModel.MyFragment2.provideConfig" },
        new Object[]{ "com.example.injector.outputs.ParameterizedCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injector.outputs.ParameterizedOutputModel",
                      "@Injector target must not contain a method with a return type that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injector.outputs.ParameterizedSupplierCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains an unexpected parameterized type. Only parameterized types known to the framework are supported" },
        new Object[]{ "com.example.injector.outputs.RawCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.RawParameterizedCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.RawParameterizedOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.RawParameterizedSupplierCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.RawSupplierCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.RawSupplierOutputModel",
                      "@Injector target must not contain a method with a return type that contains a raw type" },
        new Object[]{ "com.example.injector.outputs.WildcardCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a wildcard type parameter" },
        new Object[]{ "com.example.injector.outputs.WildcardSupplierCollectionOutputModel",
                      "@Injector target must not contain a method with a return type that contains a wildcard type parameter" },
        new Object[]{ "com.example.injector.outputs.WildcardSupplierOutputModel",
                      "@Injector target must not contain a method with a return type that contains a wildcard type parameter" },

        new Object[]{ "com.example.injector_fragment.AnnotationInjectorFragment",
                      "@InjectorFragment target must be an interface" },
        new Object[]{ "com.example.injector_fragment.ClassInjectorFragment",
                      "@InjectorFragment target must be an interface" },
        new Object[]{ "com.example.injector_fragment.DefaultMethodInInjectorFragment",
                      "@InjectorFragment target must not include default methods" },
        new Object[]{ "com.example.injector_fragment.EnumInjectorFragment",
                      "@InjectorFragment target must be an interface" },
        new Object[]{ "com.example.injector_fragment.StaticMethodInInjectorFragment",
                      "@InjectorFragment target must not include static methods" },

        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedAnnotationModel", "@Named target is not valid" },
        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedConstructorParameterModel",
                      "@Named target must only be present on a constructor parameter if the constructor is enclosed in a type annotated with @Injectable or the type is annotated with an annotation annotated by @ActAsStringComponent or @StingProvider" },
        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedEnumModel", "@Named target is not valid" },
        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedMethodModel",
                      "@Named target must not be a method unless the method is enclosed in a type annotated with @Fragment, @Injector or @InjectorFragment" },
        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedMethodParameterModel",
                      "@Named target must only be present on a method parameter if the method is enclosed in a type annotated with @Fragment" },
        new Object[]{ "com.example.unclaimed.named.UnclaimedNamedTypeModel",
                      "@Named target must only be present on a type if the type is annotated with @Injectable or the type is annotated with an annotation annotated by @ActAsStringComponent or @StingProvider" },

        new Object[]{ "com.example.unclaimed.typed.UnclaimedTypedAnnotationModel", "@Typed target is not valid" },
        new Object[]{ "com.example.unclaimed.typed.UnclaimedTypedEnumModel", "@Typed target is not valid" },
        new Object[]{ "com.example.unclaimed.typed.UnclaimedTypedMethodModel",
                      "@Typed target must not be a method unless the method is enclosed in a type annotated with @Fragment or @Injector" },
        new Object[]{ "com.example.unclaimed.typed.UnclaimedTypedTypeModel",
                      "@Typed target must only be present on a type if the type is annotated with @Injectable or the type is annotated with an annotation annotated by @StingProvider" },

        new Object[]{ "com.example.unclaimed.eager.UnclaimedEagerAnnotationModel", "@Eager target is not valid" },
        new Object[]{ "com.example.unclaimed.eager.UnclaimedEagerEnumModel", "@Eager target is not valid" },
        new Object[]{ "com.example.unclaimed.eager.UnclaimedEagerMethodModel",
                      "@Eager target must only be present on a method if the method is enclosed in a type annotated with @Fragment" },
        new Object[]{ "com.example.unclaimed.eager.UnclaimedEagerTypeModel",
                      "@Eager target must only be present on a type if the type is annotated with @Injectable or the type is annotated with an annotation annotated by @StingProvider" }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
  {
    assertFailedCompile( classname, errorMessageFragment );
  }

  @DataProvider( name = "compileWithWarnings" )
  @Nonnull
  public Object[][] compileWithWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.autofragment.IncludeAutoDiscoverableAutoFragmentModel",
                      "@ContributeTo target should not be an auto-discoverable type. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:AutoDiscoverableContributed\" )" },

        new Object[]{ "com.example.fragment.includes.IncludeAutoDiscoverableModel",
                      "@Fragment target should not include an auto-discoverable type com.example.fragment.includes.IncludeAutoDiscoverableModel.MyAutoDiscoverableModel. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:AutoDiscoverableIncluded\" )" },

        new Object[]{ "com.example.injectable.CdiTypedModel",
                      "@Injectable target must not be annotated with the javax.enterprise.inject.Typed annotation. Use the sting.Typed annotation instead. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:CdiTypedPresent\" )" },
        new Object[]{ "com.example.injectable.Jsr330InjectModel",
                      "@Injectable target must not be annotated with the javax.inject.Inject annotation. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:Jsr330InjectPresent\" )" },
        new Object[]{ "com.example.injectable.Jsr330ScopedModel",
                      "@Injectable target should not be annotated with an annotation that is annotated with the javax.inject.Scope annotation such as [@javax.inject.Singleton]. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:Jsr330ScopedPresent\" )" },

        new Object[]{ "com.example.injectable.ProtectedConstructorModel",
                      "@Injectable target should not have a protected constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:ProtectedConstructor\" )" },
        new Object[]{ "com.example.injectable.PublicConstructorModel",
                      "@Injectable target should not have a public constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:PublicConstructor\" )" },

        new Object[]{ "com.example.injectable.named.Jsr330NamedInputModel",
                      "@Injectable target must not contain a constructor with a parameter annotated with the javax.inject.Named annotation. Use the sting.Named annotation instead. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:Jsr330NamedPresent\" )" },
        new Object[]{ "com.example.injectable.named.Jsr330NamedInjectableModel",
                      "@Injectable target must not be annotated with the javax.inject.Named annotation. Use the sting.Named annotation instead. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:Jsr330NamedPresent\" )" }
      };
  }

  @Test( dataProvider = "compileWithWarnings" )
  public void processCompileWithWarnings( @Nonnull final String classname, @Nonnull final String messageFragment )
  {
    assertCompilesWithSingleWarning( classname, messageFragment );
  }

  @DataProvider( name = "compileWithoutWarnings" )
  @Nonnull
  public Object[][] compileWithoutWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "NoPackageModel" },

        new Object[]{ "com.example.autofragment.SuppressedIncludeAutoDiscoverableAutoFragmentModel" },

        new Object[]{ "com.example.contribute_to.ContributeToOnActAsStingComponentModel" },

        new Object[]{ "com.example.deprecated.DeprecatedConstructorInjectableModel" },
        new Object[]{ "com.example.deprecated.DeprecatedDependencyInjectableModel" },
        new Object[]{ "com.example.deprecated.DeprecatedDependencyInjectorModel" },
        new Object[]{ "com.example.deprecated.DeprecatedFragmentModel" },
        new Object[]{ "com.example.deprecated.DeprecatedFragmentNodeInjectorModel" },
        new Object[]{ "com.example.deprecated.DeprecatedInjectableModel" },
        new Object[]{ "com.example.deprecated.DeprecatedInjectableNodeInjectorModel" },
        new Object[]{ "com.example.deprecated.DeprecatedInjectorModel" },
        new Object[]{ "com.example.deprecated.DeprecatedProvidesDependencyModel" },
        new Object[]{ "com.example.deprecated.DeprecatedProvidesDependencyNodeInjectorModel" },
        new Object[]{ "com.example.deprecated.DeprecatedProvidesModel" },
        new Object[]{ "com.example.deprecated.DeprecatedProvidesNodeInjectorModel" },

        new Object[]{ "com.example.fragment.PackageAccessModel" },

        new Object[]{ "com.example.fragment.includes.SuppressedIncludeAutoDiscoverableModel" },

        new Object[]{ "com.example.injectable.ExposeTypesModel" },
        new Object[]{ "com.example.injectable.FinalModel" },
        new Object[]{ "com.example.injectable.PackageAccessModel" },
        new Object[]{ "com.example.injectable.SuppressedCdiTypedModel" },
        new Object[]{ "com.example.injectable.SuppressedJsr330InjectModel" },
        new Object[]{ "com.example.injectable.SuppressedJsr330ScopedModel" },
        new Object[]{ "com.example.injectable.SuppressedProtectedConstructorModel" },
        new Object[]{ "com.example.injectable.SuppressedPublicConstructorModel" },

        new Object[]{ "com.example.injectable.named.NamedProviderModel" },
        new Object[]{ "com.example.injectable.named.SuppressedJsr330NamedInputModel" },
        new Object[]{ "com.example.injectable.named.SuppressedJsr330NamedInjectableModel" },

        new Object[]{ "com.example.injector.AutodetectInjectableModel" },

        new Object[]{ "com.example.named.NamedOnActAsStingComponentModel" },
        new Object[]{ "com.example.named.NamedOnCtorParamInActAsStingComponentModel" },
        new Object[]{ "com.example.named.NamedOnInjectorFragment" }
      };
  }

  @Test
  public void processRedundantInjectableInFragmentModel()
  {
    final Compilation compilation =
      compile( Arrays.asList( input( "input",
                                     "com.example.fragment.includes.redundant_injectable.RedundantInjectableInFragmentModel" ),
                              input( "input", "com.example.fragment.includes.redundant_injectable.MyModel" ),
                              input( "input", "com.example.fragment.includes.redundant_injectable.MyFragment" ) ) );
    assertCompilationSuccessful( compilation );
    assertDiagnosticCount( compilation, Diagnostic.Kind.ERROR, 0 );
    assertWarningDiagnostic( compilation,
                             "@Fragment target should not include type com.example.fragment.includes.redundant_injectable.MyModel as it is already transitively included via included fragments. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:RedundantExplicitInjectableInclude\" )" );
    assertDiagnosticCount( compilation, Diagnostic.Kind.WARNING, 1 );
  }

  @Test
  public void processSuppressedRedundantInjectableInFragmentModel()
  {
    final Compilation compilation =
      compile( Arrays.asList( input( "input",
                                     "com.example.fragment.includes.redundant_injectable.SuppressedRedundantInjectableInFragmentModel" ),
                              input( "input", "com.example.fragment.includes.redundant_injectable.MyModel" ),
                              input( "input", "com.example.fragment.includes.redundant_injectable.MyFragment" ) ) );
    assertCompilationSuccessful( compilation );
    assertDiagnosticCount( compilation, Diagnostic.Kind.ERROR, 0 );
    assertDiagnosticCount( compilation, Diagnostic.Kind.WARNING, 0 );
  }

  @Test
  public void processRedundantInjectableInInjectorModel()
  {
    final Compilation compilation =
      compile( Arrays.asList( input( "input",
                                     "com.example.injector.includes.redundant_injectable.RedundantInjectableInInjectorModel" ),
                              input( "input", "com.example.injector.includes.redundant_injectable.MyModel" ),
                              input( "input", "com.example.injector.includes.redundant_injectable.MyFragment" ) ) );
    assertCompilationSuccessful( compilation );
    assertDiagnosticCount( compilation, Diagnostic.Kind.ERROR, 0 );
    assertWarningDiagnostic( compilation,
                             "@Injector target should not include type com.example.injector.includes.redundant_injectable.MyModel as it is already transitively included via included fragments. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:RedundantExplicitInjectableInclude\" )" );
    assertDiagnosticCount( compilation, Diagnostic.Kind.WARNING, 1 );
  }

  @Test
  public void processSuppressedRedundantInjectableInInjectorModel()
  {
    final Compilation compilation =
      compile( Arrays.asList( input( "input",
                                     "com.example.injector.includes.redundant_injectable.SuppressedRedundantInjectableInInjectorModel" ),
                              input( "input", "com.example.injector.includes.redundant_injectable.MyModel" ),
                              input( "input", "com.example.injector.includes.redundant_injectable.MyFragment" ) ) );
    assertCompilationSuccessful( compilation );
    assertDiagnosticCount( compilation, Diagnostic.Kind.ERROR, 0 );
    assertDiagnosticCount( compilation, Diagnostic.Kind.WARNING, 0 );
  }

  @Test( dataProvider = "compileWithoutWarnings" )
  public void processCompileWithoutWarnings( @Nonnull final String classname )
  {
    assertCompilesWithoutWarnings( classname );
  }

  @Test
  public void duplicateAutoFragmentKey()
  {
    final JavaFileObject source1 = input( "bad_input", "com.example.autofragment.duplicate_key.MyAutoFragment1Model" );
    final JavaFileObject source2 = input( "bad_input", "com.example.autofragment.duplicate_key.MyAutoFragment2Model" );

    assertFailedCompileResource( Arrays.asList( source1, source2 ),
                                 "@AutoFragment target must not have the same key as an existing AutoFragment of type com.example.autofragment.duplicate_key.MyAutoFragment1Model" );
  }

  @Test
  public void autodetectInjectableHasNonMatchingQualifier()
    throws IOException
  {
    final Compilation stage1 =
      compile( Collections.singletonList( input( "bad_input", "com.example.injector.autodetect.MyModel1" ) ) );

    final Path targetDir = Files.createTempDirectory( "sting" );
    CompileTestUtil.outputFiles( stage1.classOutputFilenames(), stage1.classOutput(), targetDir );

    final List<File> classPath = buildClasspath( targetDir.toFile() );
    final Compilation stage2 =
      CompileTestUtil.compile( Collections.singletonList( input( "bad_input",
                                                                 "com.example.injector.autodetect.MyInjector" ) ),
                               getOptions(),
                               processors(),
                               classPath );

    assertFalse( stage2.success() );

    assertErrorDiagnostic( stage2,
                           "@Injector target must not contain a non-optional dependency [com.example.injector.autodetect.MyModel1;qualifier='BadQualifier'] that can not be satisfied.\n" +
                           "  Dependency Path:\n" +
                           "    [Injector]       com.example.injector.autodetect.MyInjector" );
    assertErrorDiagnostic( stage2,
                           "StingProcessor failed to process 1 types. See earlier warnings for further details." );
  }

  @Test
  public void unresolvedInjector()
  {
    // This occurs when the actual class itself is unresolved
    final String classname = "com.example.injector.UnresolvedInjectorModel";
    final JavaFileObject source1 = input( "unresolved", classname );
    assertFailedCompileResource( Collections.singletonList( source1 ),
                                 "StingProcessor unable to process com.example.injector.UnresolvedInjectorModel because not all of its dependencies could be resolved. Check for compilation errors or a circular dependency with generated code." );
  }

  @Test
  public void fragmentCreatedInLaterRound()
    throws Exception
  {
    // This verifies that a fragment created by another annotation processor will be
    // incorporated into the at the component graph without causing failures
    final Processor synthesizingProcessor =
      newSynthesizingProcessor( "com.example.multiround.fragment.MyGeneratedFragment", 0 );
    final Compilation compilation =
      CompileTestUtil.compile( inputs( "com.example.multiround.fragment.MyInjector",
                                       "com.example.multiround.fragment.MyFragment",
                                       // The following inputs exist so that the synthesizing processor has types to "process"
                                       "com.example.multiround.fragment.MyFramework",
                                       "com.example.multiround.fragment.SomeType" ),
                               getOptions(),
                               Arrays.asList( synthesizingProcessor, processor() ),
                               Collections.emptyList() );

    assertCompilationSuccessful( compilation );

    compilation.assertJavaClassPresent( "com.example.multiround.fragment.MyInjector" );
    compilation.assertJavaSourcePresent( "com.example.multiround.fragment.Sting_MyInjector" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.Sting_MyInjector" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.MyFragment" );
    compilation.assertJavaSourcePresent( "com.example.multiround.fragment.Sting_MyFragment" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.Sting_MyFragment" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.MyFramework" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.SomeType" );
    compilation.assertJavaSourcePresent( "com.example.multiround.fragment.MyGeneratedFragment" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.MyGeneratedFragment" );
    compilation.assertJavaSourcePresent( "com.example.multiround.fragment.Sting_MyGeneratedFragment" );
    compilation.assertJavaClassPresent( "com.example.multiround.fragment.Sting_MyGeneratedFragment" );
  }

  @Test
  public void injectableCreatedInLaterRound()
    throws Exception
  {
    // This verifies that a fragment created by another annotation processor will be
    // incorporated into the at the component graph without causing failures
    final Processor synthesizingProcessor =
      newSynthesizingProcessor( "com.example.multiround.injectable.MyGeneratedInjectable", 0 );
    final Compilation compilation =
      CompileTestUtil.compile( inputs( "com.example.multiround.injectable.MyInjector",
                                       "com.example.multiround.injectable.MyFragment",
                                       // The following inputs exist so that the synthesizing processor has types to "process"
                                       "com.example.multiround.fragment.MyFramework",
                                       "com.example.multiround.fragment.SomeType" ),
                               getOptions(),
                               Arrays.asList( processor(), synthesizingProcessor ),
                               Collections.emptyList() );

    assertCompilationSuccessful( compilation );

    compilation.assertJavaClassPresent( "com.example.multiround.injectable.MyInjector" );
    compilation.assertJavaSourcePresent( "com.example.multiround.injectable.Sting_MyInjector" );
    compilation.assertJavaClassPresent( "com.example.multiround.injectable.Sting_MyInjector" );
    compilation.assertJavaClassPresent( "com.example.multiround.injectable.MyFragment" );
    compilation.assertJavaSourcePresent( "com.example.multiround.injectable.Sting_MyFragment" );
    compilation.assertJavaClassPresent( "com.example.multiround.injectable.Sting_MyFragment" );
    compilation.assertJavaSourcePresent( "com.example.multiround.injectable.MyGeneratedInjectable" );
    compilation.assertJavaClassPresent( "com.example.multiround.injectable.MyGeneratedInjectable" );
    compilation.assertJavaSourcePresent( "com.example.multiround.injectable.Sting_MyGeneratedInjectable" );
    compilation.assertJavaClassPresent( "com.example.multiround.injectable.Sting_MyGeneratedInjectable" );
  }

  @Test
  public void contributorCreatedInLaterRound()
    throws Exception
  {
    final Processor synthesizingProcessor =
      newSynthesizingProcessor( "com.example.multiround.autofragment.MyFrameworkModelImpl", 0 );
    final Compilation compilation =
      CompileTestUtil.compile( inputs( "com.example.multiround.autofragment.MyAutoFragment",
                                       "com.example.multiround.autofragment.MyInjectableModel",
                                       "com.example.multiround.autofragment.MyFragment",
                                       "com.example.multiround.autofragment.MyFrameworkModel",
                                       // The following input exists so that the synthesizing processor has types to "process"
                                       "com.example.multiround.autofragment.MyFramework" ),
                               getOptions(),
                               Arrays.asList( synthesizingProcessor, processor() ),
                               Collections.emptyList() );

    assertCompilationSuccessful( compilation );

    compilation.assertJavaSourcePresent( "com.example.multiround.autofragment.Sting_MyAutoFragment_Fragment" );
    compilation.assertJavaSourcePresent( "com.example.multiround.autofragment.Sting_Sting_MyAutoFragment_Fragment" );
  }

  @Test
  public void contributorCreatedAfterFragmentGenerated()
    throws Exception
  {
    // Round 0 collects all contributors
    // Fragment generates in round 1
    // Synthesized contributor generated in round 1
    // Round 2 generates an error as contributor attempts to att to generated autofragment

    final String pkg = "com.example.multiround.autofragment.already_generated";

    // This one is just used to keep synthesizer running
    final Processor synthesizingProcessor1 =
      newSynthesizingProcessor( "bad_input", pkg + ".MyFrameworkModel2", 0 );
    // this synthesizer produces java file that we are using in test
    final Processor synthesizingProcessor2 =
      newSynthesizingProcessor( "bad_input", pkg + ".MyFrameworkModelImpl", 1 );
    // this can be ignored as just keeping synthesizer going
    final Processor synthesizingProcessor3 =
      newSynthesizingProcessor( "bad_input", pkg + ".MyFrameworkModel2Impl", 1 );
    final List<JavaFileObject> inputs =
      Arrays.asList( input( "bad_input", pkg + ".MyAutoFragment" ),
                     input( "bad_input", pkg + ".MyInjectableModel" ),
                     // The following input exists so that the synthesizing processor has types to "process"
                     input( "bad_input", pkg + ".MyFramework" ) );
    final List<Processor> processors =
      Arrays.asList( synthesizingProcessor1,
                     synthesizingProcessor2,
                     synthesizingProcessor3,
                     processor() );
    final Compilation compilation =
      CompileTestUtil.compile( inputs, getOptions(), processors, Collections.emptyList() );

    assertCompilationUnsuccessful( compilation );

    final String errorMessage =
      "@ContributeTo target attempted to be added to the @AutoFragment annotated type com.example.multiround.autofragment.already_generated.MyAutoFragment but the @AutoFragment annotated type has already generated fragment";
    assertErrorDiagnostic( compilation, errorMessage );
  }

  @Nonnull
  private Compilation compileInjector( @Nonnull final Path targetDir )
  {
    return CompileTestUtil.compile( Collections.singletonList( input( "unresolved",
                                                                      "com.example.injector.UnresolvedElementsInjectorModel" ) ),
                                    getOptions(),
                                    processors(),
                                    buildClasspath( targetDir.toFile() ) );
  }

  @Nonnull
  private Path compileBindings()
    throws IOException
  {
    final Compilation compilation =
      compile( Arrays.asList( input( "unresolved", "com.example.injector.MyModel" ),
                              input( "unresolved", "com.example.injector.MyFragment" ) ) );

    assertCompilationSuccessful( compilation );

    compilation.assertClassFileCount( 4L );
    assertJsonDescriptorCount( compilation, 2L );
    assertJsonDescriptorCount( compilation, 2L );
    compilation.assertJavaFileCount( 2L );

    final Path targetDir = Files.createTempDirectory( "sting" );
    CompileTestUtil.outputFiles( compilation.classOutputFilenames(), compilation.classOutput(), targetDir );
    return targetDir;
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.emit_json_descriptors=true" );
    options.add( "-Asting.emit_dot_reports=true" );
    options.add( "-Asting.debug=true" );
    return options;
  }
}
