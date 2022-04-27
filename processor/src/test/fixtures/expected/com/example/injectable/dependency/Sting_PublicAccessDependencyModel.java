package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PublicAccessDependencyModel {
  private Sting_PublicAccessDependencyModel() {
  }

  @Nonnull
  public static PublicAccessDependencyModel create(
      final PublicAccessDependencyModel.MyType1 instance,
      final Supplier<PublicAccessDependencyModel.MyType2> supplier,
      final Collection<PublicAccessDependencyModel.MyType3> collection,
      final Collection<Supplier<PublicAccessDependencyModel.MyType4>> supplierCollection) {
    return new PublicAccessDependencyModel( Objects.requireNonNull( instance ), Objects.requireNonNull( supplier ), Objects.requireNonNull( collection ), Objects.requireNonNull( supplierCollection ) );
  }
}
