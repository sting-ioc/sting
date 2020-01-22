package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { MissingDependencyModel.MyFragment1.class,
                        MissingDependencyModel.MyFragment2.class } )
abstract class MissingDependencyModel
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
    default MyModel2 provideMyModel2( @Nullable MyModel3 model )
    {
      return new MyModel2( model );
    }
  }

  static class MyModel2
  {
    MyModel2( @Nullable MyModel3 model )
    {
    }
  }

  @Fragment
  public interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3( MyModel4 model )
    {
      return new MyModel3( model );
    }
  }

  static class MyModel3
  {
    MyModel3( MyModel4 model )
    {
    }
  }

  static class MyModel4
  {
  }
}
