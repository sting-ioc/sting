package com.example.injector.outputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_BoxedProviderPrimitiveOutputModel implements BoxedProviderPrimitiveOutputModel {
  private final BoxedProviderPrimitiveOutputModel_Sting_MyFragment fragment1 = new BoxedProviderPrimitiveOutputModel_Sting_MyFragment();

  private @Nullable Integer node1;

  Sting_BoxedProviderPrimitiveOutputModel() {
  }

  private synchronized Integer node1() {
    if ( null == node1 ) {
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Override
  public int getValue() {
    return node1();
  }
}
