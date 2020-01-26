package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("sting.processor.StingProcessor")
public final class Sting_PublicAccessDependencyModel implements PublicAccessDependencyModel {
  public PublicAccessDependencyModel.MyType1 $sting$_provideMyType1(
      final PublicAccessDependencyModel.MyType2 v) {
    return provideMyType1( Objects.requireNonNull( v ) );
  }

  public PublicAccessDependencyModel.MyType2 $sting$_provideMyType2(
      final Supplier<PublicAccessDependencyModel.MyType3> v) {
    return provideMyType2( Objects.requireNonNull( v ) );
  }

  public PublicAccessDependencyModel.MyType3 $sting$_provideMyType3(
      final Collection<PublicAccessDependencyModel.MyType4> v) {
    return provideMyType3( Objects.requireNonNull( v ) );
  }

  public PublicAccessDependencyModel.MyType4 $sting$_provideMyType4(
      final Collection<Supplier<PublicAccessDependencyModel.MyType5>> v) {
    return provideMyType4( Objects.requireNonNull( v ) );
  }
}
