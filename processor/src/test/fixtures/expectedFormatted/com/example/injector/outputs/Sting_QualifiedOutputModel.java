package com.example.injector.outputs;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_QualifiedOutputModel implements QualifiedOutputModel {
  private @Nullable Object node1;

  Sting_QualifiedOutputModel() {}

  private synchronized Object node1() {
    if (null == node1) {
      node1 = QualifiedOutputModel_Sting_MyModel.create();
    }
    return node1;
  }

  @Override
  public QualifiedOutputModel.MyModel getMyModel() {
    return (QualifiedOutputModel.MyModel) node1();
  }
}
