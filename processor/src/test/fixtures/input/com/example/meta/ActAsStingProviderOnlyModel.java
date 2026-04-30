package com.example.meta;

import sting.ActAsStingProvider;

public final class ActAsStingProviderOnlyModel
{
  @ActAsStingProvider
  @interface FrameworkComponent
  {
  }

  private ActAsStingProviderOnlyModel()
  {
  }
}
