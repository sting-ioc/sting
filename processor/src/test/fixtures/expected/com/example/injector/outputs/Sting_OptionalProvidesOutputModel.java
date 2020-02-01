package com.example.injector.outputs;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalProvidesOutputModel implements OptionalProvidesOutputModel {
  @Nonnull
  private final OptionalProvidesOutputModel_Sting_MyFragment fragment1 = new OptionalProvidesOutputModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  private boolean node1_allocated;

  Sting_OptionalProvidesOutputModel() {
  }

  @Nullable
  private Object node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Override
  @Nullable
  public OptionalProvidesOutputModel.MyModel getMyModel() {
    return (OptionalProvidesOutputModel.MyModel) node1();
  }
}
