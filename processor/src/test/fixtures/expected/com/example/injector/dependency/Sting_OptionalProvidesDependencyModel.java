package com.example.injector.dependency;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_OptionalProvidesDependencyModel implements OptionalProvidesDependencyModel {
  @Nonnull
  private final OptionalProvidesDependencyModel_Sting_MyFragment fragment1 = new OptionalProvidesDependencyModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  private boolean $sting$_node1_allocated;

  private Sting_OptionalProvidesDependencyModel() {
  }

  private Object node1() {
    if ( !$sting$_node1_allocated ) {
      $sting$_node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Override
  @Nullable
  public OptionalProvidesDependencyModel.MyModel getMyModel() {
    return (OptionalProvidesDependencyModel.MyModel) node1();
  }
}
