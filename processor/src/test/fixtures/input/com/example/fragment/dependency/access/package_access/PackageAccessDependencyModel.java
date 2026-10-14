package com.example.fragment.dependency.access.package_access;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface PackageAccessDependencyModel {
    default MyType1 provideMyType1(MyType2 v) {
        throw new UnsupportedOperationException();
    }

    default MyType2 provideMyType2(Supplier<MyType3> v) {
        throw new UnsupportedOperationException();
    }

    default MyType3 provideMyType3(Collection<MyType4> v) {
        throw new UnsupportedOperationException();
    }

    default MyType4 provideMyType4(Collection<Supplier<MyType5>> v) {
        throw new UnsupportedOperationException();
    }
}
