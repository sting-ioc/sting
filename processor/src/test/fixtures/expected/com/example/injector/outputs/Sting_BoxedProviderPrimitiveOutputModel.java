package com.example.injector.outputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_BoxedProviderPrimitiveOutputModel implements BoxedProviderPrimitiveOutputModel {
  @Nonnull
  private final BoxedProviderPrimitiveOutputModel_Sting_MyFragment fragment1 = new BoxedProviderPrimitiveOutputModel_Sting_MyFragment();

  @Nullable
  private Integer node1;

  Sting_BoxedProviderPrimitiveOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Integer node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment1.$sting$_provideValue() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public int getValue() {
    return node1();
  }
}
