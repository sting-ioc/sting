package com.example.injector.includes.injector;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_MyOtherInjectorModel implements MyOtherInjectorModel {
  @Nonnull
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  @Nullable
  private MyModel node1;

  Sting_MyOtherInjectorModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized MyModel node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public MyModel getMyModel() {
    return node1();
  }
}
