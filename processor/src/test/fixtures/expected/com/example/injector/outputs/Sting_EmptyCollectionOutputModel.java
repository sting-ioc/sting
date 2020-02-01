package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_EmptyCollectionOutputModel implements EmptyCollectionOutputModel {
  Sting_EmptyCollectionOutputModel() {
  }

  @Override
  public Collection<EmptyCollectionOutputModel.MyModel> getMyModel() {
    return Collections.emptyList();
  }
}
