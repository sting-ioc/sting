package com.example.meta;

import sting.ActAsStingComponent;

public final class ActAsStingComponentOnlyModel
{
  @ActAsStingComponent
  @interface FrameworkComponent
  {
  }

  private ActAsStingComponentOnlyModel()
  {
  }
}
