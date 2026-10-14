package com.example.fragment.dependency.access.public_access;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;

@NullMarked
@Generated("sting.processor.StingProcessor")
public final class Sting_PublicAccessDependencyModel implements PublicAccessDependencyModel {
  public MyType1 $sting$_provideMyType1(final MyType2 v) {
    return provideMyType1(v);
  }

  public MyType2 $sting$_provideMyType2(final Supplier<MyType3> v) {
    return provideMyType2(v);
  }

  public MyType3 $sting$_provideMyType3(final Collection<MyType4> v) {
    return provideMyType3(v);
  }

  public MyType4 $sting$_provideMyType4(final Collection<Supplier<MyType5>> v) {
    return provideMyType4(v);
  }
}
