package com.example.injector.outputs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_CollectionContainingMultipleInstancesOutputModel
    implements CollectionContainingMultipleInstancesOutputModel {
  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment1 fragment1 =
      new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment1();

  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment2 fragment2 =
      new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment2();

  private final CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment3 fragment3 =
      new CollectionContainingMultipleInstancesOutputModel_Sting_MyFragment3();

  private @Nullable Runnable node1;

  private @Nullable Runnable node2;

  private @Nullable Runnable node3;

  private @Nullable Collection<Runnable> $sting$_getRunnablesCache;

  Sting_CollectionContainingMultipleInstancesOutputModel() {}

  @DoNotInline
  private synchronized Runnable node1() {
    if (null == node1) {
      node1 = Objects.requireNonNull(fragment1.$sting$_provideRunnable());
    }
    assert null != node1;
    return node1;
  }

  @DoNotInline
  private synchronized Runnable node2() {
    if (null == node2) {
      node2 = Objects.requireNonNull(fragment3.$sting$_provideRunnable());
    }
    assert null != node2;
    return node2;
  }

  @DoNotInline
  private synchronized Runnable node3() {
    if (null == node3) {
      node3 = Objects.requireNonNull(fragment2.$sting$_provideRunnable());
    }
    assert null != node3;
    return node3;
  }

  @Override
  public Collection<Runnable> getRunnables() {
    if (null == $sting$_getRunnablesCache) {
      $sting$_getRunnablesCache = Arrays.asList(node1(), node3(), node2());
    }
    return $sting$_getRunnablesCache;
  }
}
