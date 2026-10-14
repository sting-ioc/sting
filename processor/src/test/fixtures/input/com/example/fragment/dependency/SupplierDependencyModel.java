package com.example.fragment.dependency;

import java.util.function.Supplier;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface SupplierDependencyModel {
    default Runnable provideRunnable(Supplier<String> name) {
        throw new UnsupportedOperationException();
    }
}
