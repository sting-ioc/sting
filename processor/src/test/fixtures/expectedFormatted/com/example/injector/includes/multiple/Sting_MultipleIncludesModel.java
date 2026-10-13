package com.example.injector.includes.multiple;

import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("UnusedVariable")
@Generated("sting.processor.StingProcessor")
final class Sting_MultipleIncludesModel implements MultipleIncludesModel {
  private final Sting_MyFragment fragment1 = new Sting_MyFragment();

  private final MyModel node1;

  private @Nullable Runnable node2;

  Sting_MultipleIncludesModel() {
    node1 = Objects.requireNonNull(Sting_MyModel.create());
  }

  @DoNotInline
  private synchronized Runnable node2() {
    if (null == node2) {
      node2 = Objects.requireNonNull(fragment1.$sting$_provideRunnable());
    }
    assert null != node2;
    return node2;
  }

  @Override
  public Runnable getRunnable() {
    return node2();
  }
}
