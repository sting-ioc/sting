package com.example.fragment.includes.provider_cycle;

import sting.Fragment;
import sting.StingProvider;

public interface ProviderIncludeCycleModel
{
  @StingProvider( "[FlatEnclosingName][SimpleName]Impl" )
  @interface MyFrameworkFragment {}

  @MyFrameworkFragment
  class A {}

  @MyFrameworkFragment
  class B {}

  @MyFrameworkFragment
  class C {}

  // Mixed provider/direct cycle: AImpl -> (provider) BImpl -> (direct) CImpl -> (provider) AImpl
  @Fragment( includes = B.class )
  interface AImpl {}

  @Fragment( includes = CImpl.class )
  interface BImpl {}

  @Fragment( includes = A.class )
  interface CImpl {}
}
