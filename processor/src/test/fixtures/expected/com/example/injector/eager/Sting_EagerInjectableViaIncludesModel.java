package com.example.injector.eager;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
final class Sting_EagerInjectableViaIncludesModel implements EagerInjectableViaIncludesModel {
  @Nonnull
  private final Object node1;

  @Nonnull
  private final Object node2;

  Sting_EagerInjectableViaIncludesModel() {
    node1 = Objects.requireNonNull( EagerInjectableViaIncludesModel_Sting_MyModel3.create() );
    node2 = Objects.requireNonNull( EagerInjectableViaIncludesModel_Sting_MyModel1.create() );
  }
}
