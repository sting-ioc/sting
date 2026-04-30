package sting.integration;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.ActAsStingProvider;
import sting.Eager;
import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.StingProvider;
import sting.Typed;
import static org.testng.Assert.*;

public final class ProviderInjectableAnnotatedAutoDiscoveryIntegrationTest
  extends AbstractIntegrationTest
{
  public interface MyService
  {
  }

  @Injectable
  public static class Model2
    extends BaseModel
  {
  }

  @ActAsStingProvider
  @StingProvider( "[CompoundName]Impl" )
  @interface MyFrameworkComponent
  {
  }

  @MyFrameworkComponent
  @Eager
  @Named( "someName" )
  @Typed( MyService.class )
  public static abstract class Model1
    extends BaseModel
    implements MyService
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

  @Injectable
  @Typed( Model1.class )
  public static class Model1Impl
    extends Model1
  {
    Model1Impl( @Nonnull final Model2 model2 )
    {
      super( model2 );
    }
  }

  @Injector
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new ProviderInjectableAnnotatedAutoDiscoveryIntegrationTest_Sting_MyInjector();
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

    assertCreateTrace( "Model2[] Model1Impl[Model2]" );

    final Model2 model2 = injector.getModel2();
    assertNotNull( model2 );
    assertSame( model2, model1.getModel2() );

    assertCreateTrace( "Model2[] Model1Impl[Model2]" );
  }
}
