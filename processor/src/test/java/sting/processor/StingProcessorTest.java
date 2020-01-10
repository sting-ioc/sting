package sting.processor;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import org.realityforge.proton.qa.AbstractProcessorTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StingProcessorTest
  extends AbstractProcessorTest
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
        new Object[]{ "com.example.fragment.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.NullableDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierDependencyModel" },

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
        new Object[]{ "com.example.injectable.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.NullableDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.injectable.dependency.QualifiedDependencyModel" },
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
    assertSuccessfulCompile( classname, toFilename( "expected", classname, "", ".sting.json" ) );
  }

  @Test
  public void nestedInjectable()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.injectable.NestedModel",
                             "expected/com/example/injectable/NestedModel_MyModel.sting.json" );
  }

  @Test
  public void nestedNestedInjectable()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.injectable.NestedNestedModel",
                             "expected/com/example/injectable/NestedNestedModel_Middle_MyModel.sting.json" );
  }

  @Test
  public void nestedFragment()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.fragment.NestedModel",
                             "expected/com/example/fragment/NestedModel_MyModel.sting.json" );
  }

  @Test
  public void nestedNestedFragment()
    throws Exception
  {
    assertSuccessfulCompile( "com.example.fragment.NestedNestedModel",
                             "expected/com/example/fragment/NestedNestedModel_Middle_MyModel.sting.json" );
  }

  @Test
  public void basicIncludesFragment()
    throws Exception
  {
    final JavaFileObject input1 = fixture( toFilename( "input", "com.example.fragment.includes.BasicIncludesModel" ) );
    final JavaFileObject input2 = fixture( toFilename( "input", "com.example.fragment.includes.Included1Model" ) );
    final String output1 =
      toFilename( "expected", "com.example.fragment.includes.BasicIncludesModel", "", ".sting.json" );
    assertSuccessfulCompile( Arrays.asList( input1, input2 ), Collections.singletonList( output1 ) );
  }

  @Test
  public void multipleIncludesFragment()
    throws Exception
  {
    final JavaFileObject input1 =
      fixture( toFilename( "input", "com.example.fragment.includes.MultipleIncludesModel" ) );
    final JavaFileObject input2 = fixture( toFilename( "input", "com.example.fragment.includes.Included1Model" ) );
    final JavaFileObject input3 = fixture( toFilename( "input", "com.example.fragment.includes.Included2Model" ) );
    final JavaFileObject input4 = fixture( toFilename( "input", "com.example.fragment.includes.Included3Model" ) );
    final String output1 =
      toFilename( "expected", "com.example.fragment.includes.MultipleIncludesModel", "", ".sting.json" );
    assertSuccessfulCompile( Arrays.asList( input1, input2, input3, input4 ), Collections.singletonList( output1 ) );
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
        new Object[]{ "com.example.fragment.NoProvidesOrIncludesModel",
                      "@Fragment target must contain one or more methods or one or more includes" },
        new Object[]{ "com.example.fragment.ParameterizedModel", "@Fragment target must not have type parameters" },

        new Object[]{ "com.example.fragment.includes.BadTypesInIncludesModel",
                      "@Fragment target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment, @Injectable or @Factory" },

        new Object[]{ "com.example.fragment.id.DuplicateIdModel",
                      "@Provides target must have a unique id but it has the same id as the method named runnable2" },

        new Object[]{ "com.example.fragment.provides.AbstractMethodProvidesModel",
                      "@Provides target must have a default modifier" },
        new Object[]{ "com.example.fragment.provides.BadReturnTypeProvidesModel",
                      "@Provides target must return a value" },
        new Object[]{ "com.example.fragment.provides.ParameterizedProvidesModel",
                      "@Provides target must not have any type parameters" },

        new Object[]{ "com.example.fragment.provides.dependency.ParameterizedDependencyModel",
                      "@Fragment target must not have a method with a parameter that is a parameterized type. This is only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.fragment.provides.dependency.RawParameterizedDependencyModel",
                      "@Fragment target must not have a method with a parameter that is a raw parameterized type. Parameterized types are only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.fragment.provides.dependency.RawSupplierDependencyModel",
                      "@Fragment target must not have a method with a parameter that is a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.fragment.provides.dependency.WildcardSupplierDependencyModel",
                      "@Fragment target must not have a method with a parameter that is a java.util.function.Supplier type with a wildcard parameter" },

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

        new Object[]{ "com.example.injectable.dependency.ParameterizedDependencyModel",
                      "@Injectable target must not have a constructor with a parameter that is a parameterized type. This is only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.injectable.dependency.RawParameterizedDependencyModel",
                      "@Injectable target must not have a constructor with a parameter that is a raw parameterized type. Parameterized types are only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.injectable.dependency.RawSupplierDependencyModel",
                      "@Injectable target must not have a constructor with a parameter that is a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.injectable.dependency.WildcardSupplierDependencyModel",
                      "@Injectable target must not have a constructor with a parameter that is a java.util.function.Supplier type with a wildcard parameter" },

        new Object[]{ "com.example.injectable.types.BadType1Model",
                      "@Injectable target has a type parameter containing the value java.lang.Runnable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType2Model",
                      "@Injectable target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType3Model",
                      "@Injectable target has a type parameter containing the value com.example.injectable.types.BadType3Model.MyOuterInterface that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.NoTypesAndLazyModel",
                      "@Injectable target must have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" },

        new Object[]{ "com.example.injector.ConcreteClassInjector",
                      "@Injector target must not must be abstract if the target is a class" },
        new Object[]{ "com.example.injector.EnumInjector",
                      "@Injector target must be an interface or an abstract class" },
        new Object[]{ "com.example.injector.MultipleConstructorClassInjector",
                      "@Injector target must not have multiple constructors" },

        new Object[]{ "com.example.injector.dependency.MethodReturningVoidDependencyModel",
                      "@Dependency target must return a value" },
        new Object[]{ "com.example.injector.dependency.MethodWithParametersDependencyModel",
                      "@Dependency target must not have any parameters" },
        new Object[]{ "com.example.injector.dependency.MethodWithTypeParametersDependencyModel",
                      "@Dependency target must not have any type parameters" },
        new Object[]{ "com.example.injector.dependency.ParameterizedDependencyModel",
                      "@Dependency target must not return a value that is a parameterized type. This is only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.injector.dependency.RawParameterizedDependencyModel",
                      "@Dependency target must not return a value that is a raw parameterized type. Parameterized types are only permitted for specific types such as java.util.function.Supplier" },
        new Object[]{ "com.example.injector.dependency.RawSupplierDependencyModel",
                      "@Dependency target must not return a value that is a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.injector.dependency.WildcardSupplierDependencyModel",
                      "@Dependency target must not return a value that is a java.util.function.Supplier type with a wildcard parameter" },

        new Object[]{ "com.example.injector.includes.BadTypesInIncludesModel",
                      "@Injector target has an includes parameter containing the value java.util.EventListener that is not a type annotated by either @Fragment, @Injectable or @Factory" }
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
                      "@Injectable target should not have a public constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:PublicConstructor\" )" },

        new Object[]{ "com.example.injector.ProtectedConstructorInjector",
                      "@Injector target should not have a protected constructor when the type is not public. The constructor is only invoked from subclasses that must be package-access as the type is not public. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:ProtectedConstructor\" )" },
        new Object[]{ "com.example.injector.PublicConstructorInjector",
                      "@Injector target should not have a public constructor. The type should not be directly instantiated and should have a protected or package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:PublicConstructor\" )" }
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

        new Object[]{ "com.example.fragment.PackageAccessModel" },

        new Object[]{ "com.example.injectable.ExposeTypesModel" },
        new Object[]{ "com.example.injectable.FinalModel" },
        new Object[]{ "com.example.injectable.PackageAccessModel" },
        new Object[]{ "com.example.injectable.SuppressedProtectedConstructorModel" },
        new Object[]{ "com.example.injectable.SuppressedPublicConstructorModel" },

        new Object[]{ "com.example.injector.SuppressedProtectedConstructorInjector" },
        new Object[]{ "com.example.injector.SuppressedPublicConstructorInjector" }
      };
  }

  @Test( dataProvider = "compileWithoutWarnings" )
  public void processCompileWithoutWarnings( @Nonnull final String classname )
  {
    assertCompilesWithoutWarnings( classname );
  }

  @Nonnull
  @Override
  protected Processor processor()
  {
    return new StingProcessor();
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "sting";
  }
}
