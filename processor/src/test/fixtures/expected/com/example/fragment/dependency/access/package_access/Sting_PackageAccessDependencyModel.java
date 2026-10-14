package com.example.fragment.dependency.access.package_access;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PackageAccessDependencyModel implements PackageAccessDependencyModel {
  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType1(final Object v) {
    return provideMyType1( (MyType2) v );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType2(final Supplier v) {
    return provideMyType2( (Supplier<MyType3>) v );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType3(final Collection v) {
    return provideMyType3( (Collection<MyType4>) v );
  }

  @SuppressWarnings({
      "rawtypes",
      "unchecked"
  })
  public Object $sting$_provideMyType4(final Collection v) {
    return provideMyType4( (Collection<Supplier<MyType5>>) v );
  }
}
