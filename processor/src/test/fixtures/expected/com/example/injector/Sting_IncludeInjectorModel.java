package com.example.injector;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_IncludeInjectorModel implements IncludeInjectorModel {
  @Nonnull
  private final IncludeInjectorModel_Sting_MyFragment fragment1 = new IncludeInjectorModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  Sting_IncludeInjectorModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public IncludeInjectorModel.MyModel getMyModel() {
    return (IncludeInjectorModel.MyModel) node1();
  }
}
