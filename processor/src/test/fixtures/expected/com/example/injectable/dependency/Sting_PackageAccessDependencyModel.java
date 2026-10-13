package com.example.injectable.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PackageAccessDependencyModel {
  private Sting_PackageAccessDependencyModel() {
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public static PackageAccessDependencyModel create(final Object instance, final Supplier supplier,
      final Collection collection, final Collection supplierCollection) {
    return new PackageAccessDependencyModel( Objects.requireNonNull( (PackageAccessDependencyModel.MyType1) instance ), Objects.requireNonNull( (Supplier<PackageAccessDependencyModel.MyType2>) supplier ), Objects.requireNonNull( (Collection<PackageAccessDependencyModel.MyType3>) collection ), Objects.requireNonNull( (Collection<Supplier<PackageAccessDependencyModel.MyType4>>) supplierCollection ) );
  }
}
