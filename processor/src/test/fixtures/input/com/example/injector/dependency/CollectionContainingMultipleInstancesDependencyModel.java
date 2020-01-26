package com.example.injector.dependency;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector( includes = { CollectionContainingMultipleInstancesDependencyModel.MyFragment1.class,
                        CollectionContainingMultipleInstancesDependencyModel.MyFragment2.class,
                        CollectionContainingMultipleInstancesDependencyModel.MyFragment3.class } )
interface CollectionContainingMultipleInstancesDependencyModel
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
