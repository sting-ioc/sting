package com.example.fragment.dependency;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface SupplierCollectionDependencyModel {
    default Runnable provideRunnable(Collection<Supplier<String>> name) {
        throw new UnsupportedOperationException();
    }
}
