package com.example.fragment.dependency;

import java.util.Optional;
import java.util.function.Supplier;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface SupplierOptionalDependencyModel {
    default Runnable provideRunnable(Supplier<Optional<String>> name) {
        throw new UnsupportedOperationException();
    }
}
