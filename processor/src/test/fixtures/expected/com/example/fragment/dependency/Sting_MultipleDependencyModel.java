package com.example.fragment.dependency;

import java.util.Objects;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_MultipleDependencyModel implements MultipleDependencyModel {
  public Runnable $sting$_provideRunnable(final String name, final int priority) {
    return provideRunnable( Objects.requireNonNull( name ), Objects.requireNonNull( priority ) );
  }
}
