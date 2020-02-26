package com.example.injector.includes;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Generated("sting.processor.StingProcessor")
final class Sting_ExplicitIncludesOfNestedModel implements ExplicitIncludesOfNestedModel {
  @Nonnull
  private final ExplicitIncludesOfNestedModel_Sting_MyFragment fragment1 = new ExplicitIncludesOfNestedModel_Sting_MyFragment();

  @Nullable
  private Object node1;

  @Nullable
  private Runnable node2;

  Sting_ExplicitIncludesOfNestedModel() {
  }

  @Nonnull
  @DoNotInline
  private Object node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( ExplicitIncludesOfNestedModel_Sting_MyModel.create() );
    }
    assert null != node1;
    return node1;
  }

  @Nonnull
  @DoNotInline
  private Runnable node2() {
    if ( null == node2 ) {
      node2 = Objects.requireNonNull( fragment1.$sting$_provideRunnable() );
    }
    assert null != node2;
    return node2;
  }

  @Override
  public Runnable getRunnable() {
    return node2();
  }

  @Override
  public ExplicitIncludesOfNestedModel.MyModel getMyModel() {
    return (ExplicitIncludesOfNestedModel.MyModel) node1();
  }
}
