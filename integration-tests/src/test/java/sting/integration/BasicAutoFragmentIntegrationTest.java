package sting.integration;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.AutoFragment;
import sting.ContributeTo;
import sting.Eager;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import static org.testng.Assert.*;

public final class BasicAutoFragmentIntegrationTest
  extends AbstractIntegrationTest
{
  @Injectable
  public static class Model1
    extends BaseModel
  {
  }

  @ContributeTo( "BasicAutoFragment.MyAutoFragment" )
  @Injectable
  @Eager
  public static class Model2
    extends BaseModel
  {
    Model2( @Nonnull final Model1 model1 )
    {
      super( model1 );
    }
  }

  public static class Model4
    extends BaseModel
  {
    Model4( @Nonnull final Model1 model1, @Nonnull final Model3 model3 )
    {
      super( model1, model3 );
    }
  }

  @Injectable
  public static class Model3
    extends BaseModel
  {
  }

  @ContributeTo( "BasicAutoFragment.MyAutoFragment" )
  @Fragment
  public interface MyFragment
  {
    default Model4 provideModel4( @Nonnull final Model1 model1, @Nonnull final Model3 model3 )
    {
      return new Model4( model1, model3 );
    }
  }

  @AutoFragment( "BasicAutoFragment.MyAutoFragment" )
  public interface MyAutoFragment
  {
  }

  @Injector( includes = MyAutoFragment.class )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new BasicAutoFragmentIntegrationTest_Sting_MyInjector();
    }

    Model4 getModel4();
  }

  @Test
  public void scenario()
  {
    clearTrace();
    final MyInjector injector = MyInjector.create();

    // Expected as Model2 is @Eager and contributes to MyAutoFragment and depends on Model1
    assertCreateTrace( "Model1[] Model2[Model1]" );

    final Model4 model4 = injector.getModel4();
    assertNotNull( model4 );

    assertCreateTrace( "Model1[] Model2[Model1] Model3[] Model4[Model1, Model3]" );

    final Model4 model4B = injector.getModel4();
    assertNotNull( model4B );
    assertSame( model4B, model4 );

    assertCreateTrace( "Model1[] Model2[Model1] Model3[] Model4[Model1, Model3]" );
  }
}
