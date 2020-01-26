package com.example.injector.dependency.eager;

import java.util.Objects;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_BasicEagerDependencyModel {
  @Nonnull
  private final Object node1;

  @Nonnull
  private final Object node2;

  @Nonnull
  private final Object node3;

  @Nonnull
  private final Object node4;

  @Nonnull
  private final Object node5;

  @Nullable
  private Object node6;

  @Nullable
  private Object node7;

  private boolean $sting$_node6_allocated;

  private boolean $sting$_node7_allocated;

  private Sting_BasicEagerDependencyModel() {
    node5 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel2.create() );
    node4 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel1.create() );
    node3 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel5.create(node5, () -> node6(), node4) );
    node2 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel4.create(node5, () -> node6(), () -> node4) );
    node1 = Objects.requireNonNull( BasicEagerDependencyModel_Sting_MyModel6.create(node2, node3) );
  }

  private Object node6() {
    if ( !$sting$_node6_allocated ) {
      $sting$_node6_allocated = true;
      node6 = BasicEagerDependencyModel_Sting_MyModel3.create(node7(), node5);
    }
    return node6;
  }

  private Object node7() {
    if ( !$sting$_node7_allocated ) {
      $sting$_node7_allocated = true;
      node7 = BasicEagerDependencyModel_Sting_MyModel0.create();
    }
    return node7;
  }
}
