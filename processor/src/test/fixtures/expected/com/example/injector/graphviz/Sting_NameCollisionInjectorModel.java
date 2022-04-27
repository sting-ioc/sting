package com.example.injector.graphviz;

import com.example.injector.graphviz.pkg2.MyModel;
import com.example.injector.graphviz.pkg2.Sting_MyModel;
import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_NameCollisionInjectorModel implements NameCollisionInjectorModel {
  @Nullable
  private MyModel node1;

  @Nullable
  private com.example.injector.graphviz.pkg1.MyModel node2;

  Sting_NameCollisionInjectorModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized MyModel node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private synchronized com.example.injector.graphviz.pkg1.MyModel node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( com.example.injector.graphviz.pkg1.Sting_MyModel.create() );
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
