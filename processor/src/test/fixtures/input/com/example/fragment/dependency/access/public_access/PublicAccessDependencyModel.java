package com.example.fragment.dependency.access.public_access;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
public interface PublicAccessDependencyModel
{
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
