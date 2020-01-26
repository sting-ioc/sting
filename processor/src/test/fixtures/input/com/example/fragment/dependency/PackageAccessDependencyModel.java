package com.example.fragment.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface PackageAccessDependencyModel
{
  class T
  {
    interface MyType1
    {
    }

    interface MyType2
    {
    }

    interface MyType3
    {
    }

    interface MyType4
    {
    }

    interface MyType5
    {
    }
  }

  default T.MyType1 provideMyType1( T.MyType2 v )
  {
    return null;
  }

  default T.MyType2 provideMyType2( Supplier<T.MyType3> v )
  {
    return null;
  }

  default T.MyType3 provideMyType3( Collection<T.MyType4> v )
  {
    return null;
  }

  default T.MyType4 provideMyType4( Collection<Supplier<T.MyType5>> v )
  {
    return null;
  }
}
