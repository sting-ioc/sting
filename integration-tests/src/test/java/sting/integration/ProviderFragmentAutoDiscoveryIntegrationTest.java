package sting.integration;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.StingProvider;
import static org.testng.Assert.*;

public final class ProviderFragmentAutoDiscoveryIntegrationTest
  extends AbstractIntegrationTest
{
  @Injectable
  public static class Model2
    extends BaseModel
  {
  }

  @StingProvider( "[CompoundName]Fragment" )
  @interface MyFrameworkComponent
  {
  }

  @MyFrameworkComponent
  public static class Model1
    extends BaseModel
  {
    @Nonnull
    private final Model2 _model2;

    Model1( @Nonnull final Model2 model2 )
    {
      super( model2 );
      _model2 = model2;
    }

    @Nonnull
    Model2 getModel2()
    {
      return _model2;
    }
  }

  @Fragment
  public interface Model1Fragment
  {
    @Nonnull
    default Model1 provideModel1( @Nonnull final Model2 model2 )
    {
      return new Model1( model2 );
    }
  }

  @Injector
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new ProviderFragmentAutoDiscoveryIntegrationTest_Sting_MyInjector();
    }

    @Nonnull
    Model1 getModel1();

    @Nonnull
    Model2 getModel2();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();

    assertCreateTrace( "" );

    final Model1 model1 = injector.getModel1();
    assertNotNull( model1 );

    assertCreateTrace( "Model2[] Model1[Model2]" );

    final Model2 model2 = injector.getModel2();
    assertNotNull( model2 );
    assertSame( model2, model1.getModel2() );

    assertCreateTrace( "Model2[] Model1[Model2]" );
  }
}
