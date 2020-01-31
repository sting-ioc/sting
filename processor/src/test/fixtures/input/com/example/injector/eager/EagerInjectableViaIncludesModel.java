package com.example.injector.eager;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
interface EagerInjectableViaIncludesModel
{
  @Fragment( includes = { MyFragment2.class, MyModel2.class } )
  interface MyFragment1
  {
  }

  @Injectable( eager = true )
  class MyModel1
  {
  }

  @Fragment( includes = MyModel3.class )
  interface MyFragment2
  {
  }

  @Injectable
  class MyModel2
  {
  }

  @Injectable( eager = true )
  class MyModel3
  {
  }
}
