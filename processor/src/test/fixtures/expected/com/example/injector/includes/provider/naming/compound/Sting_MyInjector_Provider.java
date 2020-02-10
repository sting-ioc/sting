package com.example.injector.includes.provider.naming.compound;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import sting.Fragment;

@Generated("sting.processor.StingProcessor")
@Fragment
public interface Sting_MyInjector_Provider {
  @Nonnull
  default MyInjector provide() {
    return new Sting_MyInjector();
  }

  default MyModel1 getMyModel1(final MyInjector injector) {
    return injector.getMyModel1();
  }

  default Outer.Middle.Leaf.MyModel2 getMyModel2(final MyInjector injector) {
    return injector.getMyModel2();
  }
}
