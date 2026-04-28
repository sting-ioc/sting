package sting.integration;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Factory;
import sting.Injectable;
import sting.Injector;
import static org.testng.Assert.*;

public final class FactoryIntegrationTest
  extends AbstractIntegrationTest
{
  @Injectable
  public static class SomeService
    extends BaseModel
  {
  }

  public static class MyComponent
    extends BaseModel
  {
    @Nonnull
    private final SomeService _someService;
    private final int _someParameter;

    MyComponent( @Nonnull final SomeService someService, final int someParameter )
    {
      super( someService, someParameter );
      _someService = someService;
      _someParameter = someParameter;
    }

    @Nonnull
    SomeService getSomeService()
    {
      return _someService;
    }

    int getSomeParameter()
    {
      return _someParameter;
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    @Nonnull
    MyComponent create( int someParameter );
  }

  @Injector( includes = MyComponentFactory.class )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new FactoryIntegrationTest_Sting_MyInjector();
    }

    @Nonnull
    MyComponentFactory getFactory();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();

    assertCreateTrace( "" );

    final MyComponentFactory factory = injector.getFactory();
    assertNotNull( factory );

    assertCreateTrace( "SomeService[]" );

    final MyComponent component1 = factory.create( 23 );
    assertEquals( component1.getSomeParameter(), 23 );

    assertCreateTrace( "SomeService[] MyComponent[SomeService, 23]" );

    final MyComponent component2 = factory.create( 42 );
    assertEquals( component2.getSomeParameter(), 42 );
    assertNotSame( component1, component2 );
    assertSame( component1.getSomeService(), component2.getSomeService() );
    assertSame( factory, injector.getFactory() );

    assertCreateTrace( "SomeService[] MyComponent[SomeService, 23] MyComponent[SomeService, 42]" );
  }
}
