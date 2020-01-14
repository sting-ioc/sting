package com.example.injector.dependency.eager;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
abstract class BasicEagerDependencyModel
{
  abstract MyModel6 getMyModel();

  // This will be eager as dependency via Supplier dependency and instance from Model4 and Model5 respectively
  @Injectable
  static class MyModel1
  {
  }

  // This will not be eager as only dependency from non-eager
  @Injectable
  static class MyModel2
  {
  }

  // This will not be eager as only dependency via Supplier dependency from both Model4 and Model5
  @Injectable
  static class MyModel3
  {
    MyModel3( MyModel2 model )
    {
    }
  }

  // This will be eager as required by MyModel6 which is eager
  @Injectable
  static class MyModel4
  {
    MyModel4( MyModel2 modelA, Supplier<MyModel3> modelB, Supplier<MyModel1> modelC )
    {
    }
  }

  // This will be eager as required by MyModel6 which is eager
  @Injectable
  static class MyModel5
  {
    MyModel5( MyModel2 modelA, Supplier<MyModel3> modelB, MyModel1 modelC )
    {
    }
  }

  @Injectable( eager = true )
  static class MyModel6
  {
    MyModel6( MyModel4 modelA, MyModel5 modelB )
    {
    }
  }
}
