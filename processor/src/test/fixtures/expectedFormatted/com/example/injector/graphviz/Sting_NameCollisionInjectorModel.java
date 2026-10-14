package com.example.injector.graphviz;

import com.example.injector.graphviz.pkg2.MyModel;
import com.example.injector.graphviz.pkg2.Sting_MyModel;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_NameCollisionInjectorModel implements NameCollisionInjectorModel {
  private @Nullable MyModel node1;

  private com.example.injector.graphviz.pkg1.@Nullable MyModel node2;

  Sting_NameCollisionInjectorModel() {}

  private synchronized MyModel node1() {
    if (null == node1) {
      node1 = Sting_MyModel.create();
    }
    return node1;
  }

  private synchronized com.example.injector.graphviz.pkg1.MyModel node2() {
    if (null == node2) {
      node2 = com.example.injector.graphviz.pkg1.Sting_MyModel.create();
    }
    return node2;
  }

  @Override
  public com.example.injector.graphviz.pkg1.MyModel getMyModel1() {
    return node2();
  }

  @Override
  public MyModel getMyModel2() {
    return node1();
  }
}
