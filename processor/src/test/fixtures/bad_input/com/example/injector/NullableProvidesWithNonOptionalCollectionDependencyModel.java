package com.example.injector;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface NullableProvidesWithNonOptionalCollectionDependencyModel
{
  MyModel1 getMyModel1();

  @Injectable
  class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Fragment
  interface MyFragment1
  {
    default MyModel2 provideMyModel2( /* This should be @Nullable annotated */ Collection<MyModel3> models )
    {
      return new MyModel2( models );
    }
  }

  class MyModel2
  {
    MyModel2( Collection<MyModel3> models )
    {
    }
  }

  @Fragment
  interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  @Fragment
  interface MyFragment3
  {
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  @Fragment
  interface MyFragment4
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  class MyModel3
  {
  }
}
