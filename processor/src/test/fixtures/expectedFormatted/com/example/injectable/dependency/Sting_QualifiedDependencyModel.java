package com.example.injectable.dependency;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_QualifiedDependencyModel {
  private Sting_QualifiedDependencyModel() {}

  public static QualifiedDependencyModel create(final Runnable runnable) {
    return new QualifiedDependencyModel(runnable);
  }
}
