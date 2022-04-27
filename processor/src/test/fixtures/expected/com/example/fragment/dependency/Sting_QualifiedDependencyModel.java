package com.example.fragment.dependency;

import java.util.Objects;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_QualifiedDependencyModel implements QualifiedDependencyModel {
  public Runnable $sting$_provideRunnable(final String name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
