package com.example.meta;

import sting.StingProvider;

public final class StingProviderOnlyModel
{
  @StingProvider( "[SimpleName]Impl" )
  @interface FrameworkComponent
  {
  }

  private StingProviderOnlyModel()
  {
  }
}
