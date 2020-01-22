package com.example.injector;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment1.class,
                        NullableProvidesWithNonOptionalSingularDependencyModel.MyFragment2.class } )
abstract class NullableProvidesWithNonOptionalSingularDependencyModel
{
  abstract MyModel1 getMyModel1();

  @Injectable
  static class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Fragment
  public interface MyFragment1
  {
    default MyModel2 provideMyModel2( /* This should be @Nullable annotated */ MyModel3 model )
    {
      return new MyModel2( model );
    }
  }

  static class MyModel2
  {
    MyModel2( MyModel3 model )
    {
    }
  }

  @Fragment
  public interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  static class MyModel3
  {
  }
}
