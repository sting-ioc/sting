package com.example.fragment.dependency;

import java.util.Objects;
import java.util.Optional;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_OptionalDependencyModel implements OptionalDependencyModel {
  @SuppressWarnings({"ImpossibleNullComparison", "UnnecessaryCheckNotNull"})
  public Runnable $sting$_provideRunnable(final Optional<String> name) {
    return provideRunnable(Objects.requireNonNull(name));
  }
}
