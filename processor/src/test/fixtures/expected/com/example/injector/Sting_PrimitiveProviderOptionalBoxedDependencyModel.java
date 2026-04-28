package com.example.injector;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveProviderOptionalBoxedDependencyModel implements PrimitiveProviderOptionalBoxedDependencyModel {
  @Nonnull
  private final PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyFragment fragment1 = new PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyFragment();

  private int node1;

  private boolean node1_allocated;

  @Nullable
  private Object node2;

  Sting_PrimitiveProviderOptionalBoxedDependencyModel() {
  }

  @DoNotInline
  private synchronized int node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel.create(node1()) );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public PrimitiveProviderOptionalBoxedDependencyModel.MyModel getMyModel() {
    return (PrimitiveProviderOptionalBoxedDependencyModel.MyModel) node2();
  }
}
