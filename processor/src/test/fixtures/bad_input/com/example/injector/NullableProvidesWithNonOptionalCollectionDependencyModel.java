package com.example.injector;

import java.util.Collection;
import javax.annotation.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector( includes = { NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment1.class,
                        NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment2.class,
                        NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment3.class,
                        NullableProvidesWithNonOptionalCollectionDependencyModel.MyFragment4.class } )
abstract class NullableProvidesWithNonOptionalCollectionDependencyModel
{
  abstract MyModel1 getMyModel1();

  @Injectable
  static class MyModel1
  {
    MyModel1( MyModel2 model )
    {
    }
  }

  @Fragment
  public interface MyFragment1
  {
    default MyModel2 provideMyModel2( /* This should be @Nullable annotated */ Collection<MyModel3> models )
    {
      return new MyModel2( models );
    }
  }

  static class MyModel2
  {
    MyModel2( Collection<MyModel3> models )
    {
    }
  }

  @Fragment
  public interface MyFragment2
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  @Fragment
  public interface MyFragment3
  {
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  @Fragment
  public interface MyFragment4
  {
    // Nullable provides
    @Nullable
    default MyModel3 provideMyModel3()
    {
      return new MyModel3();
    }
  }

  static class MyModel3
  {
  }
}
