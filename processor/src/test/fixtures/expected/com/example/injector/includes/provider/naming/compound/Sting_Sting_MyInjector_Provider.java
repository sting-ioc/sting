package com.example.injector.includes.provider.naming.compound;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_MyInjector_Provider implements Sting_MyInjector_Provider {
  @Nonnull
  public Object $sting$_provide() {
    return provide();
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_getMyModel1(final Object injector) {
    return getMyModel1( Objects.requireNonNull( (MyInjector) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Outer.Middle.Leaf.MyModel2 $sting$_getMyModel2(final Object injector) {
    return getMyModel2( Objects.requireNonNull( (MyInjector) injector ) );
  }
}
