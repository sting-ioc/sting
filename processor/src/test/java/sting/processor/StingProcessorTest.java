package sting.processor;

import javax.annotation.Nonnull;
import javax.annotation.processing.Processor;
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

        new Object[]{ "com.example.fragment.dependency.BasicDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.ComplexDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.MultipleDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.NullableDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.PrimitiveDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.QualifiedDependencyModel" },
        new Object[]{ "com.example.fragment.dependency.SupplierDependencyModel" },

        new Object[]{ "com.example.fragment.eager.EagerModel" },
        new Object[]{ "com.example.fragment.eager.LazyModel" },

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
                      "@Fragment target has an includes parameter containing the value java.util.EventListener that is not a type annotated by @Fragment" },

        new Object[]{ "com.example.fragment.provides.AbstractMethodProvidesModel",
                      "@Fragment target must only contain methods with the default modifier" },
        new Object[]{ "com.example.fragment.provides.BadReturnTypeProvidesModel",
                      "@Fragment target must only contain methods that return a value" },

        new Object[]{ "com.example.fragment.provides.dependency.RawSupplierDependencyModel",
                      "@Fragment target must not have a method parameter that is a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.fragment.provides.dependency.WildcardSupplierDependencyModel",
                      "@Fragment target must not have a method parameter that is a java.util.function.Supplier type with a wildcard parameter" },

        new Object[]{ "com.example.fragment.provides.types.BadType1Model",
                      "@Provides target has a type parameter containing the value java.lang.Runnable that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType2Model",
                      "@Provides target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.BadType3Model",
                      "@Provides target has a type parameter containing the value com.example.fragment.provides.types.BadType3Model.MyOuterInterface that is not assignable to the return type of the method" },
        new Object[]{ "com.example.fragment.provides.types.NoTypesAndLazyModel",
                      "@Provides target must have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" },

        new Object[]{ "com.example.injectable.AbstractModel", "@Injectable target must not be abstract" },
        new Object[]{ "com.example.injectable.MultipleConstructorModel",
                      "@Injectable target must not have multiple constructors" },
        new Object[]{ "com.example.injectable.NonStaticNestedModel",
                      "@Injectable target must not be a non-static nested class" },
        new Object[]{ "com.example.injectable.ParameterizedModel", "@Injectable target must not have type parameters" },

        new Object[]{ "com.example.injectable.dependency.RawSupplierDependencyModel",
                      "@Injectable target must not have a constructor parameter that is a raw java.util.function.Supplier type" },
        new Object[]{ "com.example.injectable.dependency.WildcardSupplierDependencyModel",
                      "@Injectable target must not have a constructor parameter that is a java.util.function.Supplier type with a wildcard parameter" },

        new Object[]{ "com.example.injectable.types.BadType1Model",
                      "@Injectable target has a type parameter containing the value java.lang.Runnable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType2Model",
                      "@Injectable target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.BadType3Model",
                      "@Injectable target has a type parameter containing the value com.example.injectable.types.BadType3Model.MyOuterInterface that is not assignable to the declaring type" },
        new Object[]{ "com.example.injectable.types.NoTypesAndLazyModel",
                      "@Injectable target must have one or more types specified or must specify eager = true otherwise the binding will never be used by the injector" }
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

        new Object[]{ "com.example.fragment.PackageAccessModel" },

        new Object[]{ "com.example.injectable.ExposeTypesModel" },
        new Object[]{ "com.example.injectable.FinalModel" },
        new Object[]{ "com.example.injectable.PackageAccessModel" },
        new Object[]{ "com.example.injectable.SuppressedProtectedConstructorModel" },
        new Object[]{ "com.example.injectable.SuppressedPublicConstructorModel" }
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
