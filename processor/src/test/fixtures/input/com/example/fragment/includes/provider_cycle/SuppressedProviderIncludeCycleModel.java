package com.example.fragment.includes.provider_cycle;

import sting.Fragment;
import sting.StingProvider;

public interface SuppressedProviderIncludeCycleModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface MyFrameworkFragment {}

  @MyFrameworkFragment
  class A {}

  @MyFrameworkFragment
  class B {}

  @MyFrameworkFragment
  class C {}

  @SuppressWarnings( "Sting:FragmentIncludeCycle" )
  @Fragment( includes = B.class )
  interface AImpl {}

  @SuppressWarnings( "Sting:FragmentIncludeCycle" )
  @Fragment( includes = CImpl.class )
  interface BImpl {}

  @SuppressWarnings( "Sting:FragmentIncludeCycle" )
  @Fragment( includes = A.class )
  interface CImpl {}
}
