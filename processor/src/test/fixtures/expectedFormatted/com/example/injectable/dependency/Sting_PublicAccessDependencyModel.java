package com.example.injectable.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PublicAccessDependencyModel {
  private Sting_PublicAccessDependencyModel() {}

  public static PublicAccessDependencyModel create(
      final PublicAccessDependencyModel.MyType1 instance,
      final Supplier<PublicAccessDependencyModel.MyType2> supplier,
      final Collection<PublicAccessDependencyModel.MyType3> collection,
      final Collection<Supplier<PublicAccessDependencyModel.MyType4>> supplierCollection) {
    return new PublicAccessDependencyModel(instance, supplier, collection, supplierCollection);
  }
}
