package com.example.fragment.dependency;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface SupplierOptionalCollectionDependencyModel {
    default Runnable provideRunnable(Collection<Supplier<Optional<String>>> name) {
        throw new UnsupportedOperationException();
    }
}
