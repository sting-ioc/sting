package com.example.injector.includes.diamond;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;

@Generated("sting.processor.StingProcessor")
final class Sting_DiamondDependencyIncludesModel implements DiamondDependencyIncludesModel {
  @Nonnull
  private final Object node1;

  Sting_DiamondDependencyIncludesModel() {
    node1 = Objects.requireNonNull( Sting_MyModel.create() );
  }

  @Override
  public MyModel getRunnable() {
    return (MyModel) node1;
  }
}
