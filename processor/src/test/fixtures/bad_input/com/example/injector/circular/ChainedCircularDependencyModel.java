package com.example.injector.circular;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { ChainedCircularDependencyModel.MyFragment1.class,
                        ChainedCircularDependencyModel.MyFragment2.class } )
abstract class ChainedCircularDependencyModel
{
  abstract MyModel1 getMyModel1();

  abstract MyModel2 getMyModel2();

  @Nullable
  abstract MyModel3 getMyModel3();

  abstract MyModel4 getMyModel4();

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

  @Injectable
  static class MyModel4
  {
    MyModel4( MyModel1 model )
    {
    }
  }
}
