package com.example.injector.graphviz;

import com.example.injector.graphviz.pkg2.MyModel;
import com.example.injector.graphviz.pkg2.Sting_MyModel;
import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_NameCollisionInjectorModel implements NameCollisionInjectorModel {
  private @Nullable MyModel node1;

  private com.example.injector.graphviz.pkg1.@Nullable MyModel node2;

  Sting_NameCollisionInjectorModel() {}

  @DoNotInline
  private synchronized MyModel node1() {
    if (null == node1) {
      node1 = Objects.requireNonNull(Sting_MyModel.create());
    }
    assert null != node1;
    return node1;
  }

  @DoNotInline
  private synchronized com.example.injector.graphviz.pkg1.MyModel node2() {
    if (null == node2) {
      node2 = Objects.requireNonNull(com.example.injector.graphviz.pkg1.Sting_MyModel.create());
    }
    assert null != node2;
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
