package sting.integration;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import static org.testng.Assert.*;

public final class InputsIntegrationTest
  extends AbstractIntegrationTest
{
  public static class Model1
    extends BaseModel
  {
  }

  @Injectable
  public static class Model2
    extends BaseModel
  {
    Model2( @Nonnull final Model1 model1 )
    {
      super( model1 );
    }
  }

  public static class Model3
    extends BaseModel
  {
    Model3( @Nonnull final Model1 model1, @Nonnull final Model2 model2 )
    {
      super( model1, model2 );
    }
  }

  @Fragment
  public interface MyFragment
  {
    default Model3 provideModel3( @Nonnull final Model1 model1, @Nonnull final Model2 model2 )
    {
      return new Model3( model1, model2 );
    }
  }

  @Injector( includes = MyFragment.class, inputs = @Injector.Input( type = Model1.class ) )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create( @Nonnull final Model1 model1 )
    {
      return new InputsIntegrationTest_Sting_MyInjector( model1 );
    }

    Model2 getModel2();

    Model3 getModel3();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create( new Model1() );

    assertCreateTrace( "Model1[]" );

    final Model2 model2 = injector.getModel2();
    assertNotNull( model2 );

    assertCreateTrace( "Model1[] Model2[Model1]" );

    final Model3 model3 = injector.getModel3();
    assertNotNull( model3 );

    assertCreateTrace( "Model1[] Model2[Model1] Model3[Model1, Model2]" );

    final Model3 model3b = injector.getModel3();
    assertNotNull( model3b );
    assertSame( model3b, model3 );

    assertCreateTrace( "Model1[] Model2[Model1] Model3[Model1, Model2]" );
  }

  @Test
  public void scenario_onlyAccessModel3()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create( new Model1() );

    assertCreateTrace( "Model1[]" );

    final Model3 model3 = injector.getModel3();
    assertNotNull( model3 );

    final String expected = "Model1[] Model2[Model1] Model3[Model1, Model2]";
    assertCreateTrace( expected );
  }
}
