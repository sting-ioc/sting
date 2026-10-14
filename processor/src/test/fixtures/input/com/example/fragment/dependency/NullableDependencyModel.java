package com.example.fragment.dependency;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullableDependencyModel {
    default Runnable provideRunnable(@Nullable String name) {
        throw new UnsupportedOperationException();
    }
}
