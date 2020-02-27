package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_EmptyCollectionOutputModel implements EmptyCollectionOutputModel {
  private Collection<EmptyCollectionOutputModel.MyModel> $sting$_getMyModelCache;

  Sting_EmptyCollectionOutputModel() {
  }

  @Override
  public Collection<EmptyCollectionOutputModel.MyModel> getMyModel() {
    if ( null == $sting$_getMyModelCache ) {
      $sting$_getMyModelCache = Collections.emptyList();
    }
    return $sting$_getMyModelCache;
  }
}
