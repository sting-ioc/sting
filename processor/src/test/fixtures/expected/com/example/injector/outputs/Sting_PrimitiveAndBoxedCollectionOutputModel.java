package com.example.injector.outputs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveAndBoxedCollectionOutputModel implements PrimitiveAndBoxedCollectionOutputModel {
  @Nonnull
  private final PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment1 fragment1 = new PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment1();

  @Nonnull
  private final PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment2 fragment2 = new PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment2();

  @Nullable
  private Integer node1;

  private int node2;

  private boolean node2_allocated;

  private Collection<Integer> $sting$_getValuesCache;

  Sting_PrimitiveAndBoxedCollectionOutputModel() {
  }

  @Nonnull
  @DoNotInline
  private synchronized Integer node1() {
    if ( null == node1 ) {
      node1 = Objects.requireNonNull( fragment2.$sting$_provideValue() );
    }
    assert null != node1;
    return node1;
  }

  @DoNotInline
  private synchronized int node2() {
    if ( !node2_allocated ) {
      node2_allocated = true;
      node2 = fragment1.$sting$_provideValue();
    }
    return node2;
  }

  @Override
  public Collection<Integer> getValues() {
    if ( null == $sting$_getValuesCache ) {
      $sting$_getValuesCache = Arrays.asList( node2(), node1() );
    }
    return $sting$_getValuesCache;
  }
}
