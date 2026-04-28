package com.example.injector.outputs;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveProviderBoxedOutputModel implements PrimitiveProviderBoxedOutputModel {
  @Nonnull
  private final PrimitiveProviderBoxedOutputModel_Sting_MyFragment fragment1 = new PrimitiveProviderBoxedOutputModel_Sting_MyFragment();

  private int node1;

  private boolean node1_allocated;

  Sting_PrimitiveProviderBoxedOutputModel() {
  }

  @DoNotInline
  private synchronized int node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Override
  public Integer getValue() {
    return node1();
  }
}
