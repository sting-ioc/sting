package com.example.injector.outputs;

import java.util.Arrays;
import java.util.Collection;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveAndBoxedCollectionOutputModel implements PrimitiveAndBoxedCollectionOutputModel {
  private final PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment1 fragment1 = new PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment1();

  private final PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment2 fragment2 = new PrimitiveAndBoxedCollectionOutputModel_Sting_MyFragment2();

  private @Nullable Integer node1;

  private int node2;

  private boolean node2_allocated;

  private @Nullable Collection<Integer> $sting$_getValuesCache;

  Sting_PrimitiveAndBoxedCollectionOutputModel() {
  }

  private synchronized Integer node1() {
    if ( null == node1 ) {
      node1 = fragment2.$sting$_provideValue();
    }
    return node1;
  }

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
