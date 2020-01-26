package com.example.fragment.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface PublicAccessDependencyModel
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

  default MyType1 provideMyType1( MyType2 v )
  {
    return null;
  }

  default MyType2 provideMyType2( Supplier<MyType3> v )
  {
    return null;
  }

  default MyType3 provideMyType3( Collection<MyType4> v )
  {
    return null;
  }

  default MyType4 provideMyType4( Collection<Supplier<MyType5>> v )
  {
    return null;
  }
}
