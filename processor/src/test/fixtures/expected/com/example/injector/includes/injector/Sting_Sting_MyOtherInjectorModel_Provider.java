package com.example.injector.includes.injector;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_MyOtherInjectorModel_Provider implements Sting_MyOtherInjectorModel_Provider {
  @Nonnull
  public MyOtherInjectorModel $sting$_provide() {
    return provide();
  }

  public MyModel $sting$_getMyModel(@Nonnull final MyOtherInjectorModel injector) {
    return getMyModel( Objects.requireNonNull( injector ) );
  }
}
