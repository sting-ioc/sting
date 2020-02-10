package com.example.injector.includes.provider.naming.enclosing;

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
  public MyModel $sting$_getMyModel(final Object injector) {
    return getMyModel( Objects.requireNonNull( (MyInjector) injector ) );
  }

  @SuppressWarnings("unchecked")
  public Outer.Middle.Leaf.MyModel2 $sting$_getMyModel2(final Object injector) {
    return getMyModel2( Objects.requireNonNull( (MyInjector) injector ) );
  }
}
