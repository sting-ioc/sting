package com.example.injector.dependency.eager;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { EagerInjectableViaIncludesModel.MyModel1.class,
                        EagerInjectableViaIncludesModel.MyFragment1.class } )
abstract class EagerInjectableViaIncludesModel
{
  @Fragment( includes = { MyFragment2.class, MyModel2.class } )
  public interface MyFragment1
  {
  }

  @Injectable( eager = true )
  static class MyModel1
  {
  }

  @Fragment( includes = MyModel3.class )
  public interface MyFragment2
  {
  }

  @Injectable
  static class MyModel2
  {
  }

  @Injectable( eager = true )
  static class MyModel3
  {
  }
}
