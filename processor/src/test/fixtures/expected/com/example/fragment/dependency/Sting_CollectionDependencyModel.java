package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_CollectionDependencyModel implements CollectionDependencyModel {
  public Runnable $sting$_provideRunnable(final Collection<String> name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
