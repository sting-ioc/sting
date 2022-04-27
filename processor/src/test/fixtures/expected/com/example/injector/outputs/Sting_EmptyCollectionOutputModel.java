package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_EmptyCollectionOutputModel implements EmptyCollectionOutputModel {
  private Collection<Runnable> $sting$_getRunnablesCache;

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
