package com.example.injector.eager.fragment;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
final class Sting_EagerInjectableViaIncludesModel implements EagerInjectableViaIncludesModel {
  @Nonnull
  private final MyModel3 node1;

  Sting_EagerInjectableViaIncludesModel() {
    node1 = Objects.requireNonNull( Sting_MyModel3.create() );
  }
}
