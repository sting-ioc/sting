package com.example.injector.dependency;

import java.util.Collection;
import java.util.Collections;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_EmptyCollectionDependencyModel implements EmptyCollectionDependencyModel {
  Sting_EmptyCollectionDependencyModel() {
  }

  @Override
  public Collection<EmptyCollectionDependencyModel.MyModel> getMyModel() {
    return Collections.emptyList();
  }
}
