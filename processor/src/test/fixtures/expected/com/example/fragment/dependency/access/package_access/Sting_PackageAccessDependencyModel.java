package com.example.fragment.dependency.access.package_access;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PackageAccessDependencyModel implements PackageAccessDependencyModel {
  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType1(final Object v) {
    return provideMyType1( Objects.requireNonNull( (MyType2) v ) );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType2(final Supplier v) {
    return provideMyType2( Objects.requireNonNull( (Supplier<MyType3>) v ) );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType3(final Collection v) {
    return provideMyType3( Objects.requireNonNull( (Collection<MyType4>) v ) );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType4(final Collection v) {
    return provideMyType4( Objects.requireNonNull( (Collection<Supplier<MyType5>>) v ) );
  }
}
