package com.example.injector.outputs;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_QualifiedOutputModel implements QualifiedOutputModel {
  @Nullable
  private Object node1;

  Sting_QualifiedOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( QualifiedOutputModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Override
  public QualifiedOutputModel.MyModel getMyModel() {
    return (QualifiedOutputModel.MyModel) node1();
  }
}
