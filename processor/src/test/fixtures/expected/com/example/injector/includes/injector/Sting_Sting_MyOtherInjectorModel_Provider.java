package com.example.injector.includes.injector;

import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_Sting_MyOtherInjectorModel_Provider implements Sting_MyOtherInjectorModel_Provider {
  public MyOtherInjectorModel $sting$_provide() {
    return provide();
  }

  public MyModel $sting$_getMyModel(final MyOtherInjectorModel injector) {
    return getMyModel( Objects.requireNonNull( injector ) );
  }
}
