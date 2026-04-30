package sting.integration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injector;
import sting.Typed;
import static org.testng.Assert.*;

public final class TypedProviderMethodIntegrationTest
  extends AbstractIntegrationTest
{
  public interface MyService
  {
  }

  public static class Model1
    extends BaseModel
    implements MyService
  {
  }

  @Fragment
  public interface MyFragment
  {
    @Typed( MyService.class )
    default Model1 provideModel1()
    {
      return new Model1();
    }
  }

  @Injector( includes = MyFragment.class )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new TypedProviderMethodIntegrationTest_Sting_MyInjector();
    }

    @Nonnull
    MyService getMyService();

    @Nullable
    Model1 getModel1();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();

    assertCreateTrace( "" );

    final MyService service1 = injector.getMyService();
    assertNotNull( service1 );
    assertTrue( service1 instanceof Model1 );

    assertCreateTrace( "Model1[]" );

    final MyService service2 = injector.getMyService();
    assertSame( service2, service1 );

    assertCreateTrace( "Model1[]" );

    final Model1 model1 = injector.getModel1();
    assertNull( model1 );

    assertCreateTrace( "Model1[]" );
  }
}
