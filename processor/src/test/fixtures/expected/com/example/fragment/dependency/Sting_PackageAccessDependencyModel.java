package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PackageAccessDependencyModel implements PackageAccessDependencyModel {
  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType1(final Object v) {
    return provideMyType1( Objects.requireNonNull( (PackageAccessDependencyModel.T.MyType2) v ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType2(final Object v) {
    return provideMyType2( Objects.requireNonNull( (Supplier<PackageAccessDependencyModel.T.MyType3>) v ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType3(final Object v) {
    return provideMyType3( Objects.requireNonNull( (Collection<PackageAccessDependencyModel.T.MyType4>) v ) );
  }

  @SuppressWarnings("unchecked")
  public Object $sting$_provideMyType4(final Object v) {
    return provideMyType4( Objects.requireNonNull( (Collection<Supplier<PackageAccessDependencyModel.T.MyType5>>) v ) );
  }
}
