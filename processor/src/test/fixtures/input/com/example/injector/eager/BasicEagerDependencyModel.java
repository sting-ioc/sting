package com.example.injector.eager;

import java.util.function.Supplier;
import sting.Eager;
import sting.Injectable;
import sting.Injector;

@Injector
interface BasicEagerDependencyModel
{
  MyModel6 getMyModel();

  // This will not be eager as only dependency from non-eager
  @Injectable
  class MyModel0
  {
  }

  // This will be eager as dependency via Supplier dependency and instance from Model4 and Model5 respectively
  @Injectable
  class MyModel1
  {
  }

  // This will be eager as a dependency of both eager and non-eager
  @Injectable
  class MyModel2
  {
  }

  // This will not be eager as only dependency via Supplier dependency from both Model4 and Model5
  @Injectable
  class MyModel3
  {
    MyModel3( MyModel0 modelA, MyModel2 modelB )
    {
    }
  }

  // This will be eager as required by MyModel6 which is eager
  @Injectable
  class MyModel4
  {
    MyModel4( MyModel2 modelA, Supplier<MyModel3> modelB, Supplier<MyModel1> modelC )
    {
    }
  }

  // This will be eager as required by MyModel6 which is eager
  @Injectable
  class MyModel5
  {
    MyModel5( MyModel2 modelA, Supplier<MyModel3> modelB, MyModel1 modelC )
    {
    }
  }

  @Eager
  @Injectable
  class MyModel6
  {
    MyModel6( MyModel4 modelA, MyModel5 modelB )
    {
    }
  }
}
