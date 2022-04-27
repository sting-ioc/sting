package com.example.injectable;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class NestedModel_Sting_MyModel {
  private NestedModel_Sting_MyModel() {
  }

  @Nonnull
  public static NestedModel.MyModel create() {
    return new NestedModel.MyModel();
  }
}
