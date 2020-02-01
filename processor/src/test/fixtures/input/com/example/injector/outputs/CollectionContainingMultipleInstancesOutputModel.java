package com.example.injector.outputs;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector
interface CollectionContainingMultipleInstancesOutputModel
{
  Collection<MyModel> getMyModel();

  class MyModel
  {
  }

  @Fragment
  interface MyFragment1
  {
    default MyModel myModel()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment2
  {
    default MyModel myModel()
    {
      return null;
    }
  }

  @Fragment
  interface MyFragment3
  {
    default MyModel myModel()
    {
      return null;
    }
  }
}
