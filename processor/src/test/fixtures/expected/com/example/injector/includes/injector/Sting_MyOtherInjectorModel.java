package com.example.injector.includes.injector;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MyOtherInjectorModel implements MyOtherInjectorModel {
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  private @Nullable MyModel node1;

  Sting_MyOtherInjectorModel() {
  }

  @DoNotInline
  private synchronized MyModel node1() {
    if ( null == node1 ) {
      node1 = fragment1.$sting$_provideRunnable();
    }
    return node1;
  }

  @Override
  public MyModel getMyModel() {
    return node1();
  }
}
