package com.example.fragment.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_MultipleDependencyModel implements MultipleDependencyModel {
  public Runnable $sting$_provideRunnable(final String name, final int priority) {
    return provideRunnable(name, priority);
  }
}
