package com.example.injector;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveProviderOptionalBoxedDependencyModel
    implements PrimitiveProviderOptionalBoxedDependencyModel {
  private final PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyFragment fragment1 =
      new PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyFragment();

  private int node1;

  private boolean node1_allocated;

  private @Nullable Object node2;

  Sting_PrimitiveProviderOptionalBoxedDependencyModel() {}

  private synchronized int node1() {
    if (!node1_allocated) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  private synchronized Object node2() {
    if (null == node2) {
      node2 = PrimitiveProviderOptionalBoxedDependencyModel_Sting_MyModel.create(node1());
    }
    return node2;
  }

  @Override
  public PrimitiveProviderOptionalBoxedDependencyModel.MyModel getMyModel() {
    return (PrimitiveProviderOptionalBoxedDependencyModel.MyModel) node2();
  }
}
