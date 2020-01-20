package com.example.injector.circular;

import java.util.function.Supplier;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = SupplierBrokenFragmentWalkingCircularDependencyModel.MyFragment.class )
abstract class SupplierBrokenFragmentWalkingCircularDependencyModel
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

  @Injectable
  static class MyModel2
  {
    MyModel2( MyModel3 model )
    {
    }
  }

  @Fragment
  public interface MyFragment
  {
    default MyModel3 provideMyModel2( Supplier<MyModel1> model )
    {
      return new MyModel3( model );
    }
  }

  static class MyModel3
  {
    MyModel3( Supplier<MyModel1> model )
    {
    }
  }
}
