package com.example.injector.includes.injector;

import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_MyInjector implements MyInjector {
  private final Sting_Sting_MyOtherInjectorModel_Provider fragment1 = new Sting_Sting_MyOtherInjectorModel_Provider();

  private @Nullable MyOtherInjectorModel node1;

  private @Nullable MyModel node2;

  Sting_MyInjector() {
  }

  @DoNotInline
  private synchronized MyOtherInjectorModel node1() {
    if ( null == node1 ) {
      node1 = fragment1.$sting$_provide();
    }
    return node1;
  }

  @DoNotInline
  private synchronized MyModel node2() {
    if ( null == node2 ) {
      node2 = fragment1.$sting$_getMyModel(node1());
    }
    return node2;
  }

  @Override
  public MyModel getMyModel() {
    return node2();
  }
}
