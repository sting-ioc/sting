package com.example.injector.autodetect.provider;

import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.StingProvider;
import sting.Typed;

public interface QualifiedPublishedTypeModel
{
  @Injector
  interface MyInjector
  {
    MyModel1 getMyModel1();
  }

  @StingProvider( "[CompoundName]Impl" )
  @interface MyFrameworkComponent
  {
  }

  @MyFrameworkComponent
  class MyModel1
  {
  }

  @Injectable
  @Named( "framework" )
  @Typed( MyModel1.class )
  class MyModel1Impl
    extends MyModel1
  {
  }
}
