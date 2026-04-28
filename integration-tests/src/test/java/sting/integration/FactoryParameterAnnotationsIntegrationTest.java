package sting.integration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.annotations.Test;
import sting.Factory;
import sting.Injectable;
import sting.Injector;
import static org.testng.Assert.*;

public final class FactoryParameterAnnotationsIntegrationTest
  extends AbstractIntegrationTest
{
  private static final class StableRunnable
    implements Runnable
  {
    @Override
    public void run()
    {
    }

    @Override
    @Nonnull
    public String toString()
    {
      return "StableRunnable";
    }
  }

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
    @Nullable
    private final String _name;
    @Nonnull
    private final Runnable _action;
    private final int _count;

    MyComponent( @Nonnull final SomeService someService,
                 @Nullable final String name,
                 @Nonnull final Runnable action,
                 final int count )
    {
      super( someService, name, action, count );
      _someService = someService;
      _name = name;
      _action = action;
      _count = count;
    }

    @Nonnull
    SomeService getSomeService()
    {
      return _someService;
    }

    @Nullable
    String getName()
    {
      return _name;
    }

    @Nonnull
    Runnable getAction()
    {
      return _action;
    }

    int getCount()
    {
      return _count;
    }
  }

  @Factory
  public interface MyComponentFactory
  {
    @Nonnull
    MyComponent create( @Nullable String name, @Nonnull Runnable action, int count );
  }

  @Injector( includes = MyComponentFactory.class )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new FactoryParameterAnnotationsIntegrationTest_Sting_MyInjector();
    }

    @Nonnull
    MyComponentFactory getFactory();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();
    final MyComponentFactory factory = injector.getFactory();
    final Runnable action = new StableRunnable();

    final MyComponent component1 = factory.create( null, action, 17 );
    assertNull( component1.getName() );
    assertSame( action, component1.getAction() );
    assertEquals( component1.getCount(), 17 );

    final MyComponent component2 = factory.create( "beta", action, 23 );
    assertEquals( component2.getName(), "beta" );
    assertSame( action, component2.getAction() );
    assertEquals( component2.getCount(), 23 );
    assertSame( component1.getSomeService(), component2.getSomeService() );

    assertCreateTrace( "SomeService[] MyComponent[SomeService, null, StableRunnable, 17] " +
                       "MyComponent[SomeService, beta, StableRunnable, 23]" );
  }
}
