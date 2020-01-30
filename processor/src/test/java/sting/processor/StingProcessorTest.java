package sting.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

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
        new Object[]{ "com.example.fragment.dependency.PackageAccessDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.PublicAccessDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierCollectionDependencyModel" },

        new Object[]{ "com.example.fragment.eager.EagerModel" },
        new Object[]{ "com.example.fragment.eager.LazyModel" },

        new Object[]{ "com.example.fragment.id.ExplicitIdModel" },

        new Object[]{ "com.example.fragment.qualifier.BasicQualifierModel" },
        new Object[]{ "com.example.fragment.qualifier.EmptyQualifierModel" },
        new Object[]{ "com.example.fragment.qualifier.NonStandardQualifierModel" },

        new Object[]{ "com.example.fragment.types.BasicTypesModel" },
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

        new Object[]{ "com.example.injectable.id.ExplicitIdModel" },

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

        new Object[]{ "com.example.injector.dependency.BasicDependencyModel" },
        new Object[]{ "com.example.injector.dependency.CollectionContainingMultipleInstancesDependencyModel" },
        new Object[]{ "com.example.injector.dependency.CollectionDependencyModel" },
        new Object[]{ "com.example.injector.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.injector.dependency.EmptyCollectionDependencyModel" },
        new Object[]{ "com.example.injector.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.injector.dependency.OptionalDependencyModel" },
        new Object[]{ "com.example.injector.dependency.OptionalMissingDependencyModel" },
        new Object[]{ "com.example.injector.dependency.OptionalProvidesDependencyModel" },
        new Object[]{ "com.example.injector.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.injector.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.injector.dependency.SupplierCollectionDependencyModel" },
        new Object[]{ "com.example.injector.dependency.SupplierDependencyModel" },

        new Object[]{ "com.example.injector.includes.ExplicitIncludesOfNestedModel" }
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

  private boolean emitInjectorGeneratedFile( @Nonnull final String classname, @Nonnull final JavaFileObject target )
  {
    final int index = classname.lastIndexOf( "." );
    final String simpleClassName = -1 == index ? classname : classname.substring( index + 1 );
    return JavaFileObject.Kind.SOURCE == target.getKind() ||
           target.getName().endsWith( simpleClassName + StingProcessor.JSON_SUFFIX ) ||
           target.getName().endsWith( simpleClassName + StingProcessor.GRAPH_SUFFIX );
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

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.dependency.UnclaimedConstructorParameterDependencyModel",
                      "@Dependency target must only be present on a parameter of a constructor if the enclosing type is annotated with @Injectable" },
        new Object[]{ "com.example.dependency.UnclaimedMethodDependencyModel",
                      "@Dependency target must not be a method unless present in a type annotated with @Injector" },
        new Object[]{ "com.example.dependency.UnclaimedMethodParameterDependencyModel",
                      "@Dependency target must only be present on a parameter of a method if the enclosing type is annotated with @Fragment" },

        new Object[]{ "com.example.fragment.ClassModel", "@Fragment target must be an interface" },
        new Object[]{ "com.example.fragment.FragmentExtendsSuperinterfaceModel",
                      "@Fragment target must not extend any interfaces" },
        new Object[]{ "com.example.fragment.NoProvidesOrIncludesModel",
                      "@Fragment target must contain one or more methods or one or more includes" },
        new Object[]{ "com.example.fragment.ParameterizedModel", "@Fragment target must not have type parameters" },

        new Object[]{ "com.example.fragment.includes.BadTypesInIncludesModel",
                      "@Fragment target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment or @Injectable" },

        new Object[]{ "com.example.fragment.id.DuplicateIdModel",
                      "@Provides target must have a unique id but it has the same id as the method named runnable2" },

        new Object[]{ "com.example.fragment.provides.AbstractMethodProvidesModel",
                      "@Provides target must have a default modifier" },
        new Object[]{ "com.example.fragment.provides.IncompatibleReturnTypeProvidesModel",
                      "@Provides target has a type parameter containing the value java.lang.Boolean that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.NullablePrimitiveReturnTypeProvidesModel",
                      "@Provides target is incorrectly annotated with @Nullable as the return type is a primitive" },
        new Object[]{ "com.example.fragment.provides.ParameterizedProvidesModel",
                      "@Provides target must not have any type parameters" },
        new Object[]{ "com.example.fragment.provides.StaticMethodProvidesModel",
                      "@Provides target must have a default modifier" },
        new Object[]{ "com.example.fragment.provides.VoidReturnTypeProvidesModel",
                      "@Provides target must return a value" },

        new Object[]{ "com.example.fragment.inputs.ArrayTypeInputModel",
                      "@Dependency target must not be an array type" },
        new Object[]{ "com.example.fragment.inputs.IncompatibleTypeInputModel",
                      "@Dependency target specifies a type parameter that is not assignable to the actual type" },
        new Object[]{ "com.example.fragment.inputs.NullableCollectionInputModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.fragment.inputs.NullableSupplierCollectionInputModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedCollectionInputModel",
                      "@Dependency target must not be a collection parameter that contains a parameterized type as the type parameter" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedInputModel",
                      "@Dependency target must not be a parameterized type other than the special types known by the framework such as java.util.function.Supplier and java.util.Collection" },
        new Object[]{ "com.example.fragment.inputs.ParameterizedSupplierCollectionInputModel",
                      "@Dependency target must not be a supplier collection with a parameterized type as the type parameter" },
        new Object[]{ "com.example.fragment.inputs.RawCollectionInputModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedCollectionInputModel",
                      "@Dependency target must not be a collection parameter that contains a parameterized type as the type parameter" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedInputModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.fragment.inputs.RawParameterizedSupplierCollectionInputModel",
                      "@Dependency target must not be a supplier collection with a parameterized type as the type parameter" },
        new Object[]{ "com.example.fragment.inputs.RawSupplierCollectionInputModel",
                      "@Dependency target must not be a raw supplier collection type" },
        new Object[]{ "com.example.fragment.inputs.RawSupplierInputModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.fragment.inputs.WildcardCollectionInputModel",
                      "@Dependency target must not be a parameterized type with a wildcard type parameter" },
        new Object[]{ "com.example.fragment.inputs.WildcardSupplierCollectionInputModel",
                      "@Dependency target must not be a supplier collection with a wildcard type parameter" },
        new Object[]{ "com.example.fragment.inputs.WildcardSupplierInputModel",
                      "@Dependency target must not be a parameterized type with a wildcard type parameter" },

        new Object[]{ "com.example.fragment.provides.types.BadType1Model",
                      "@Provides target has a type parameter containing the value java.lang.Runnable that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType2Model",
                      "@Provides target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType3Model",
                      "@Provides target has a type parameter containing the value com.example.fragment.provides.types.BadType3Model.MyOuterInterface that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.NoTypesAndLazyModel",
                      "@Provides target must have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" },

        new Object[]{ "com.example.injectable.AbstractModel", "@Injectable target must not be abstract" },
        new Object[]{ "com.example.injectable.InterfaceModel", "@Injectable target must be a class" },
        new Object[]{ "com.example.injectable.MultipleConstructorModel",
                      "@Injectable target must not have multiple constructors" },
        new Object[]{ "com.example.injectable.NonStaticNestedModel",
                      "@Injectable target must not be a non-static nested class" },
        new Object[]{ "com.example.injectable.ParameterizedModel", "@Injectable target must not have type parameters" },

        new Object[]{ "com.example.injectable.dependency.ArrayDependencyModel",
                      "@Dependency target must not be an array type" },
        new Object[]{ "com.example.injectable.dependency.IncompatibleTypeDependencyModel",
                      "@Dependency target specifies a type parameter that is not assignable to the actual type" },
        new Object[]{ "com.example.injectable.dependency.NullableCollectionDependencyModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.injectable.dependency.NullableSupplierCollectionDependencyModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.injectable.dependency.ParameterizedCollectionDependencyModel",
                      "@Dependency target must not be a collection parameter that contains a parameterized type" },
        new Object[]{ "com.example.injectable.dependency.ParameterizedSupplierCollectionDependencyModel",
                      "@Dependency target must not be a supplier collection parameter that contains a parameterized type" },
        new Object[]{ "com.example.injectable.dependency.ParameterizedDependencyModel",
                      "@Dependency target must not be a parameterized type other than the special types known by the framework such as java.util.function.Supplier and java.util.Collection" },
        new Object[]{ "com.example.injectable.dependency.RawCollectionDependencyModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.injectable.dependency.RawParameterizedCollectionDependencyModel",
                      "@Dependency target must not be a collection parameter that contains a parameterized type" },
        new Object[]{ "com.example.injectable.dependency.RawParameterizedDependencyModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.injectable.dependency.RawParameterizedSupplierCollectionDependencyModel",
                      "@Dependency target must not be a supplier collection parameter that contains a parameterized type" },
        new Object[]{ "com.example.injectable.dependency.RawSupplierCollectionDependencyModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.injectable.dependency.RawSupplierDependencyModel",
                      "@Dependency target must not be a raw parameterized type" },
        new Object[]{ "com.example.injectable.dependency.WildcardCollectionDependencyModel",
                      "@Dependency target must not be a java.util.Collection type with a wildcard type parameter" },
        new Object[]{ "com.example.injectable.dependency.WildcardSupplierCollectionDependencyModel",
                      "@Dependency target must not be a supplier collection parameter with a wildcard type parameter" },
        new Object[]{ "com.example.injectable.dependency.WildcardSupplierDependencyModel",
                      "@Dependency target must not be a java.util.function.Supplier type with a wildcard type parameter" },

        new Object[]{ "com.example.injectable.types.BadType1Model",
                      "@Injectable target has a type parameter containing the value java.lang.Runnable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType2Model",
                      "@Injectable target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType3Model",
                      "@Injectable target has a type parameter containing the value com.example.injectable.types.BadType3Model.MyOuterInterface that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.NoTypesAndLazyModel",
                      "@Injectable target must have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" },

        new Object[]{ "com.example.injector.ClassInjector", "@Injector target must be an interface" },
        new Object[]{ "com.example.injector.DuplicateIdInjectorModel",
                      "@Injector target must not contain multiple nodes with the id 'foo'.\n" +
                      "  Path:\n" +
                      "    [Injector]       com.example.injector.DuplicateIdInjectorModel" },
        new Object[]{ "com.example.injector.EnumInjector", "@Injector target must be an interface" },
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
                      "@Injector target must not contain a nullable @Provides method and a non-optional dependency [com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyModel3] with the same coordinate.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel\n" +
                      "    [Injectable]     com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyModel1\n" +
                      "    [Provides]    *  com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment1.provideMyModel2\n" +
                      "  \n" +
                      "  Bindings:\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment2.provideMyModel3\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment4.provideMyModel3" },
        new Object[]{ "com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel",
                      "@Injector target must not contain a nullable @Provides method and a non-optional dependency [com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyModel3] with the same coordinate.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel\n" +
                      "    [Injectable]     com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyModel1\n" +
                      "    [Provides]    *  com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment1.provideMyModel2\n" +
                      "  \n" +
                      "  Binding:\n" +
                      "    [Provides]       com.example.injector.NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment2.provideMyModel3" },
        new Object[]{ "com.example.injector.TypeParametersInjectorModel",
                      "@Injector target must not have type parameters" },

        new Object[]{ "com.example.injector.circular.ChainedCircularDependencyModel",
                      "Injector contains a circular dependency.\n" +
                      "  Path:\n" +
                      "    [Injector]       com.example.injector.circular.ChainedCircularDependencyModel\n" +
                      "    [Injectable] +-< com.example.injector.circular.ChainedCircularDependencyModel.MyModel1\n" +
                      "    [Provides]   |   com.example.injector.circular.ChainedCircularDependencyModel.MyFragment1.provideMyModel2\n" +
                      "    [Provides]   |   com.example.injector.circular.ChainedCircularDependencyModel.MyFragment2.provideMyModel3\n" +
                      "    [Injectable] |   com.example.injector.circular.ChainedCircularDependencyModel.MyModel4\n" +
                      "    [Injectable] +-> com.example.injector.circular.ChainedCircularDependencyModel.MyModel1" },
        new Object[]{ "com.example.injector.circular.DirectlyCircularDependencyModel",
                      "Injector contains a circular dependency.\n" +
                      "  Path:\n" +
                      "    [Injector]       com.example.injector.circular.DirectlyCircularDependencyModel\n" +
                      "    [Injectable] +-< com.example.injector.circular.DirectlyCircularDependencyModel.MyModel1\n" +
                      "    [Injectable] |   com.example.injector.circular.DirectlyCircularDependencyModel.MyModel2\n" +
                      "    [Injectable] +-> com.example.injector.circular.DirectlyCircularDependencyModel.MyModel1" },

        new Object[]{ "com.example.injector.dependency.ArrayDependencyModel",
                      "@Dependency target must not return an array type" },
        new Object[]{ "com.example.injector.dependency.IncompatibleTypeDependencyModel",
                      "@Dependency target specifies a type parameter that is not assignable to the actual type" },
        new Object[]{ "com.example.injector.dependency.NullableCollectionDependencyModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.injector.dependency.NullableSupplierCollectionDependencyModel",
                      "@Dependency target must not be annotated with @Nullable and be a collection type" },
        new Object[]{ "com.example.injector.dependency.MethodReturningVoidDependencyModel",
                      "@Dependency target must return a value" },
        new Object[]{ "com.example.injector.dependency.MethodWithParametersDependencyModel",
                      "@Dependency target must not have any parameters" },
        new Object[]{ "com.example.injector.dependency.MethodWithTypeParametersDependencyModel",
                      "@Dependency target must not have any type parameters" },
        new Object[]{ "com.example.injector.dependency.MissingDependencyModel",
                      "@Injector target must not contain a non-optional dependency [com.example.injector.dependency.MissingDependencyModel.MyModel4] that can not be satisfied.\n" +
                      "  Dependency Path:\n" +
                      "    [Injector]       com.example.injector.dependency.MissingDependencyModel\n" +
                      "    [Injectable]     com.example.injector.dependency.MissingDependencyModel.MyModel1\n" +
                      "    [Provides]       com.example.injector.dependency.MissingDependencyModel.MyFragment1.provideMyModel2\n" +
                      "    [Provides]    *  com.example.injector.dependency.MissingDependencyModel.MyFragment2.provideMyModel3" },
        new Object[]{ "com.example.injector.dependency.ParameterizedCollectionDependencyModel",
                      "@Dependency target must not return a collection type that contains a parameterized type" },
        new Object[]{ "com.example.injector.dependency.ParameterizedDependencyModel",
                      "@Dependency target must not return a value that is a parameterized type. This is only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.injector.dependency.ParameterizedSupplierCollectionDependencyModel",
                      "@Dependency target must not return a a supplier collection type that contains a parameterized type" },
        new Object[]{ "com.example.injector.dependency.RawCollectionDependencyModel",
                      "@Dependency target must not return a a raw java.util.Collection type" },
        new Object[]{ "com.example.injector.dependency.RawParameterizedCollectionDependencyModel",
                      "@Dependency target must not return a collection type that contains a parameterized type" },
        new Object[]{ "com.example.injector.dependency.RawParameterizedDependencyModel",
                      "@Dependency target must not return a raw parameterized type. Parameterized types are only permitted for specific types such as java.util.function.Supplier and java.util.Collection" },
        new Object[]{ "com.example.injector.dependency.RawParameterizedSupplierCollectionDependencyModel",
                      "@Dependency target must not return a a supplier collection type that contains a parameterized type" },
        new Object[]{ "com.example.injector.dependency.RawSupplierCollectionDependencyModel",
                      "@Dependency target must not return a supplier collection parameter that contains a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.injector.dependency.RawSupplierDependencyModel",
                      "@Dependency target must not return a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.injector.dependency.WildcardCollectionDependencyModel",
                      "@Dependency target must not return a java.util.Collection type with a wildcard type parameter" },
        new Object[]{ "com.example.injector.dependency.WildcardSupplierCollectionDependencyModel",
                      "@Dependency target must not return a supplier collection parameter with a wildcard type parameter" },
        new Object[]{ "com.example.injector.dependency.WildcardSupplierDependencyModel",
                      "@Dependency target must not return a java.util.function.Supplier type with a wildcard type parameter" },

        new Object[]{ "com.example.injector.includes.BadTypesInIncludesModel",
                      "@Injector target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment or @Injectable" }
      };
  }

  @Test( dataProvider = "failedCompiles" )
  public void processFailedCompile( @Nonnull final String classname, @Nonnull final String errorMessageFragment )
  {
    assertFailedCompile( classname, errorMessageFragment );
  }

  @DataProvider( name = "compileWithWarnings" )
  public Object[][] compileWithWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.injectable.ProtectedConstructorModel",
                      "@Injectable target should not have a protected constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:ProtectedConstructor\" )" },
        new Object[]{ "com.example.injectable.PublicConstructorModel",
                      "@Injectable target should not have a public constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:PublicConstructor\" )" }
      };
  }

  @Test( dataProvider = "compileWithWarnings" )
  public void processCompileWithWarnings( @Nonnull final String classname, @Nonnull final String messageFragment )
  {
    assertCompilesWithSingleWarning( classname, messageFragment );
  }

  @DataProvider( name = "compileWithoutWarnings" )
  public Object[][] compileWithoutWarnings()
  {
    return new Object[][]
      {
        new Object[]{ "NoPackageModel" },

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
        new Object[]{ "com.example.fragment.dependency.ExplicitTypeDependencyModel" },

        new Object[]{ "com.example.injectable.ExposeTypesModel" },
        new Object[]{ "com.example.injectable.FinalModel" },
        new Object[]{ "com.example.injectable.PackageAccessModel" },
        new Object[]{ "com.example.injectable.SuppressedProtectedConstructorModel" },
        new Object[]{ "com.example.injectable.SuppressedPublicConstructorModel" },
        new Object[]{ "com.example.injectable.dependency.ExplicitTypeDependencyModel" },

        new Object[]{ "com.example.injector.dependency.ExplicitTypeDependencyModel" }
      };
  }

  @Test( dataProvider = "compileWithoutWarnings" )
  public void processCompileWithoutWarnings( @Nonnull final String classname )
  {
    assertCompilesWithoutWarnings( classname );
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
  public void unresolvedInjectableInjectorModel()
    throws Exception
  {
    final Path targetDir = compileBindings();

    assertCompilationSuccessful( compileInjector( targetDir ) );

    final Path descriptor =
      targetDir
        .resolve( "com" )
        .resolve( "example" )
        .resolve( "injector" )
        .resolve( "MyModel.sbf" );

    Files.delete( descriptor );

    final Compilation compilation = compileInjector( targetDir );
    assertCompilationUnsuccessful( compilation );
    final ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics = compilation.diagnostics();
    assertEquals( diagnostics.size(), 2 );
    assertEquals( diagnostics.get( 0 ).getMessage( Locale.getDefault() ),
                  "StingProcessor failed to process 1 injectors " +
                  "as not all of their dependencies could be resolved. The java code resolved but the " +
                  "descriptors were missing or in the incorrect format. Ensure that the included " +
                  "typed have been compiled with a compatible version of Sting and that the .sbf " +
                  "descriptors have been packaged with the .class files." );

    assertEquals( diagnostics.get( 1 ).getMessage( Locale.getDefault() ),
                  "Failed to process the com.example.injector.UnresolvedElementsInjectorModel injector." );
  }

  @Test
  public void unresolvedFragmentInjectorModel()
    throws Exception
  {
    final Path targetDir = compileBindings();

    assertCompilationSuccessful( compileInjector( targetDir ) );

    final Path descriptor =
      targetDir
        .resolve( "com" )
        .resolve( "example" )
        .resolve( "injector" )
        .resolve( "MyFragment.sbf" );

    Files.delete( descriptor );

    final Compilation compilation = compileInjector( targetDir );
    assertCompilationUnsuccessful( compilation );
    final ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics = compilation.diagnostics();
    assertEquals( diagnostics.size(), 2 );
    assertEquals( diagnostics.get( 0 ).getMessage( Locale.getDefault() ),
                  "StingProcessor failed to process 1 injectors " +
                  "as not all of their dependencies could be resolved. The java code resolved but the " +
                  "descriptors were missing or in the incorrect format. Ensure that the included " +
                  "typed have been compiled with a compatible version of Sting and that the .sbf " +
                  "descriptors have been packaged with the .class files." );

    assertEquals( diagnostics.get( 1 ).getMessage( Locale.getDefault() ),
                  "Failed to process the com.example.injector.UnresolvedElementsInjectorModel injector." );
  }

  @Test
  public void unresolvedDueToBadDiscriptorInjectorModel()
    throws Exception
  {
    final Path targetDir = compileBindings();

    assertCompilationSuccessful( compileInjector( targetDir ) );

    final Path descriptor =
      targetDir
        .resolve( "com" )
        .resolve( "example" )
        .resolve( "injector" )
        .resolve( "MyFragment.sbf" );

    final RandomAccessFile file = new RandomAccessFile( descriptor.toFile().getAbsolutePath(), "rw" );

    // This is incorrect header
    file.seek( 0 );
    file.writeInt( 0x666 );
    file.close();

    final Compilation compilation = compileInjector( targetDir );
    assertCompilationUnsuccessful( compilation );
    final ImmutableList<Diagnostic<? extends JavaFileObject>> diagnostics = compilation.diagnostics();
    diagnostics
      .stream()
      .map( d -> d.getMessage( Locale.getDefault() ) )
      .filter( d -> d.contains( "Failed to read the Sting descriptor for include: com.example.injector.MyFragment." ) )
      .findAny()
      .orElseThrow( AssertionError::new );
  }

  @Nonnull
  private Compilation compileInjector( @Nonnull final Path targetDir )
  {
    return compiler()
      .withClasspath( buildClasspath( targetDir.toFile() ) )
      .compile( Collections.singletonList( input( "unresolved",
                                                  "com.example.injector.UnresolvedElementsInjectorModel" ) ) );
  }

  @Nonnull
  private Path compileBindings()
    throws IOException
  {
    final Compilation bindingsCompile =
      compiler()
        .compile( Arrays.asList( input( "unresolved", "com.example.injector.MyModel" ),
                                 input( "unresolved", "com.example.injector.MyFragment" ) ) );

    assertCompilationSuccessful( bindingsCompile );
    final ImmutableList<JavaFileObject> generatedFiles = bindingsCompile.generatedFiles();

    assertEquals( generatedFiles.size(), 10 );
    assertClassFileCount( generatedFiles, 4L );
    // 2 binary and 2 json descriptors
    assertDescriptorCount( generatedFiles, 4L );
    assertSourceFileCount( generatedFiles, 2L );

    final Path targetDir = Files.createTempDirectory( "sting" );
    outputFiles( bindingsCompile.generatedFiles(), targetDir );
    return targetDir;
  }

  @Nonnull
  @Override
  protected List<String> getOptions()
  {
    final List<String> options = new ArrayList<>( super.getOptions() );
    options.add( "-Asting.emit_json_descriptors=true" );
    return options;
  }

  @Override
  protected boolean emitGeneratedFile( @Nonnull final JavaFileObject target )
  {
    return super.emitGeneratedFile( target ) && !target.getName().endsWith( StingProcessor.SUFFIX );
  }
}
