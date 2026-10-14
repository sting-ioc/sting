package com.example.injector.includes.injector;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import sting.Fragment;

@NullMarked
@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MyOtherInjectorModel_Provider {
  default MyOtherInjectorModel provide() {
    return new Sting_MyOtherInjectorModel();
  }

  default MyModel getMyModel(final MyOtherInjectorModel injector) {
    return injector.getMyModel();
  }
}
