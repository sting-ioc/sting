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
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname );
  }

  @DataProvider( name = "failedCompiles" )
  public Object[][] failedCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "com.example.at_inject.AbstractModel", "@Injectable target must not be abstract" },
        new Object[]{ "com.example.at_inject.BadType1Model",
                      "@Injectable target has a type parameter containing the value java.lang.Runnable that is not assignable to the declaring type" },
        new Object[]{ "com.example.at_inject.BadType2Model",
                      "@Injectable target has a type parameter containing the value java.util.concurrent.Callable that is not assignable to the declaring type" },
        new Object[]{ "com.example.at_inject.BadType3Model",
                      "@Injectable target has a type parameter containing the value com.example.at_inject.BadType3Model.MyOuterInterface that is not assignable to the declaring type" },
        new Object[]{ "com.example.at_inject.MultipleConstructorModel", "@Injectable target must not have multiple constructors" },
        new Object[]{ "com.example.at_inject.NonStaticNestedModel", "@Injectable target must not be a non-static nested class" }
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
        new Object[]{ "com.example.at_inject.ProtectedConstructorModel",
                      "@Injectable target should not have a protected constructor. The type is instantiated by the injector and should have a package-access constructor. This warning can be suppressed by annotating the element with @SuppressWarnings( \"Sting:ProtectedConstructor\" )" },
        new Object[]{ "com.example.at_inject.PublicConstructorModel",
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
        new Object[]{ "com.example.at_inject.ExposeTypesModel" },
        new Object[]{ "com.example.at_inject.SuppressedProtectedConstructorModel" },
        new Object[]{ "com.example.at_inject.SuppressedPublicConstructorModel" }
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
