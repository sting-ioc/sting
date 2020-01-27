package com.example.injector.dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_CollectionDependencyModel implements CollectionDependencyModel {
  @Nullable
  private Object node1;

  Sting_CollectionDependencyModel() {
  }

  @Nonnull
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( CollectionDependencyModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public Collection<CollectionDependencyModel.MyModel> getMyModel() {
    return Collections.singletonList( (CollectionDependencyModel.MyModel) node1() );
  }
}
