package com.example.injector.inputs;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface MissingInputModel
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
    default MyModel2 provideMyModel2( @Nullable MyModel3 model )
    {
      return new MyModel2( model );
    }
  }

  class MyModel2
  {
    MyModel2( @Nullable MyModel3 model )
    {
    }
  }

  @Fragment
  interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3( MyModel4 model )
    {
      return new MyModel3( model );
    }
  }

  class MyModel3
  {
    MyModel3( MyModel4 model )
    {
    }
  }

  class MyModel4
  {
  }
}
