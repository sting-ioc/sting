package com.example.injector.dependency;

import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector( includes = OptionalProvidesDependencyModel.MyFragment.class )
abstract class OptionalProvidesDependencyModel
{
  @Nullable
  abstract MyModel getMyModel();

  @Fragment
  public interface MyFragment
  {
    @Nullable
    default MyModel provideValue()
    {
      return null;
    }
  }

  static class MyModel
  {
  }
}
