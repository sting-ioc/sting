package com.example.fragment.dependency;

import java.util.Optional;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface OptionalDependencyModel {
    default Runnable provideRunnable(Optional<String> name) {
        throw new UnsupportedOperationException();
    }
}
