package com.example.injector.dependency;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector( includes = { CollectionContainingMultipleInstancesDependencyModel.MyFragment1.class,
                        CollectionContainingMultipleInstancesDependencyModel.MyFragment2.class,
                        CollectionContainingMultipleInstancesDependencyModel.MyFragment3.class } )
abstract class CollectionContainingMultipleInstancesDependencyModel
{
  abstract Collection<MyModel> getMyModel();

  static class MyModel
  {
  }

  @Fragment
  public interface MyFragment1
  {
    default MyModel myModel()
    {
      return null;
    }
  }

  @Fragment
  public interface MyFragment2
  {
    default MyModel myModel()
    {
      return null;
    }
  }

  @Fragment
  public interface MyFragment3
  {
    default MyModel myModel()
    {
      return null;
    }
  }
}
