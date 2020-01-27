package sting.integration;

import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.Provides;
import static org.testng.Assert.*;

public final class MultiplePublishedTypesDependenciesIntegrationTest
  extends AbstractIntegrationTest
{
  public interface MyService1
  {
  }

  public interface MyService2
  {
  }

  public interface MyService3
  {
  }

  public interface MyService4
  {
  }

  @Injectable( eager = true, types = { MyService1.class, MyService2.class } )
  public static class Model1
    extends BaseModel
    implements MyService1, MyService2
  {
  }

  @Injectable( types = { MyService3.class, Model2.class, Object.class } )
  public static class Model2
    extends BaseModel
    implements MyService3
  {
    Model2( @Nonnull final MyService2 model1 )
    {
      super( model1 );
    }
  }

  public static class Model3
    extends BaseModel
  {
    Model3( @Nonnull final MyService2 model1, @Nonnull final MyService3 model2 )
    {
      super( model1, model2 );
    }
  }

  @Injectable( eager = true, types = {} )
  public static class Model4
    extends BaseModel
  {
  }

  @Fragment
  public interface MyFragment
  {
    @Provides( types = { Model3.class, Object.class } )
    default Model3 provideModel3( @Nonnull final MyService2 model1, @Nonnull final MyService3 model2 )
    {
      return new Model3( model1, model2 );
    }
  }

  @Injector( includes = { MyFragment.class, Model1.class, Model2.class, Model4.class } )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new MultiplePublishedTypesDependenciesIntegrationTest_Sting_MyInjector();
    }

    Model2 getModel2();

    Model3 getModel3();

    MyService1 getMyService1();

    MyService2 getMyService2();

    MyService3 getMyService3();

    // No binding present so this should produce null but not otherwise generate an error
    @Nullable
    MyService4 getMyService4();

    Collection<Object> getObjects();
  }

  @Test
  public void scenario()
  {
    final MyInjector injector = MyInjector.create();

    assertCreateTrace( "Model1[] Model4[]" );

    final Model2 model2 = injector.getModel2();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1]" );

    final Model3 model3 = injector.getModel3();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    final MyService1 service1 = injector.getMyService1();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    final MyService2 service2 = injector.getMyService2();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    final MyService3 service3 = injector.getMyService3();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    final MyService4 service4 = injector.getMyService4();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    final Collection<Object> objects = injector.getObjects();

    assertCreateTrace( "Model1[] Model4[] Model2[Model1] Model3[Model1, Model2]" );

    assertNotNull( service1 );
    assertNotNull( service2 );
    assertNotNull( service3 );
    assertNull( service4 );
    assertNotNull( model2 );
    assertNotNull( model3 );

    assertSame( service1, service2 );
    assertSame( service3, model2 );

    assertEquals( objects.size(), 2 );
    assertTrue( objects.contains( model2 ) );
    assertTrue( objects.contains( model3 ) );
  }
}
