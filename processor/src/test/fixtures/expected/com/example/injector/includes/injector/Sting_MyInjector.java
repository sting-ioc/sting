package com.example.injector.includes.injector;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  @Nonnull
  private final Sting_Sting_MyOtherInjectorModel_Provider fragment1 = new Sting_Sting_MyOtherInjectorModel_Provider();

  @Nonnull
  private final Sting_Sting_MyOtherInjectorModel_Provider fragment2 = new Sting_Sting_MyOtherInjectorModel_Provider();

  @Nullable
  private MyOtherInjectorModel node1;

  @Nullable
  private MyModel node2;

  Sting_MyInjector() {
  }

  @Nonnull
  @DoNotInline
  private synchronized MyOtherInjectorModel node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment2.$sting$_provide() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized MyModel node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( fragment2.$sting$_getMyModel(node1()) );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public MyModel getMyModel() {
    return node2();
  }
}
