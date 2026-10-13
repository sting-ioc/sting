package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_CollectionDependencyModel implements CollectionDependencyModel {
  @SuppressWarnings({
      "ImpossibleNullComparison",
      "UnnecessaryCheckNotNull"
  })
  public Runnable $sting$_provideRunnable(final Collection<String> name) {
    return provideRunnable( Objects.requireNonNull( name ) );
  }
}
