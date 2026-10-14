package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_EmptyCollectionOutputModel implements EmptyCollectionOutputModel {
  private @Nullable Collection<Runnable> $sting$_getRunnablesCache;

  Sting_EmptyCollectionOutputModel() {
  }

  @Override
  public Collection<Runnable> getRunnables() {
    if ( null == $sting$_getRunnablesCache ) {
      $sting$_getRunnablesCache = Collections.emptyList();
    }
    return $sting$_getRunnablesCache;
  }
}
