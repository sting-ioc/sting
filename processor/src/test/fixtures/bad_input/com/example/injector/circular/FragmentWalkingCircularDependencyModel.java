package com.example.injector.circular;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = FragmentWalkingCircularDependencyModel.MyFragment.class )
abstract class FragmentWalkingCircularDependencyModel
{
  abstract MyModel1 getMyModel1();

  abstract MyModel2 getMyModel2();

  abstract MyModel3 getMyModel3();

  @Injectable
  static class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Fragment
  public interface MyFragment
  {
    default MyModel2 provideMyModel2( MyModel3 model )
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

  @Injectable
  static class MyModel3
  {
    MyModel3( MyModel1 model )
    {
    }
  }
}
