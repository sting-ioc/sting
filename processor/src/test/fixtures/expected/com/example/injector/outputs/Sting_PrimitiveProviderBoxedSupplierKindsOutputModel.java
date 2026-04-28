package com.example.injector.outputs;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import javaemul.internal.annotations.DoNotInline;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
final class Sting_PrimitiveProviderBoxedSupplierKindsOutputModel implements PrimitiveProviderBoxedSupplierKindsOutputModel {
  @Nonnull
  private final PrimitiveProviderBoxedSupplierKindsOutputModel_Sting_MyFragment fragment1 = new PrimitiveProviderBoxedSupplierKindsOutputModel_Sting_MyFragment();

  private int node1;

  private boolean node1_allocated;

  private Collection<Integer> $sting$_getValuesCache;

  private Collection<Supplier<Integer>> $sting$_getValueSuppliersCache;

  Sting_PrimitiveProviderBoxedSupplierKindsOutputModel() {
  }

  @DoNotInline
  private synchronized int node1() {
    if ( !node1_allocated ) {
      node1_allocated = true;
      node1 = fragment1.$sting$_provideValue();
    }
    return node1;
  }

  @Override
  public Collection<Integer> getValues() {
    if ( null == $sting$_getValuesCache ) {
      $sting$_getValuesCache = Collections.singletonList( node1() );
    }
    return $sting$_getValuesCache;
  }

  @Override
  public Supplier<Integer> getValueSupplier() {
    return () -> node1();
  }

  @Override
  public Collection<Supplier<Integer>> getValueSuppliers() {
    if ( null == $sting$_getValueSuppliersCache ) {
      $sting$_getValueSuppliersCache = Collections.singletonList( () -> node1() );
    }
    return $sting$_getValueSuppliersCache;
  }
}
