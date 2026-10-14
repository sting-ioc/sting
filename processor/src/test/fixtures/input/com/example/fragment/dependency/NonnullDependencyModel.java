package com.example.fragment.dependency;

import org.jspecify.annotations.NullMarked;
import sting.Fragment;

@Fragment
@NullMarked
public interface NonnullDependencyModel {
    default Runnable provideRunnable(String name) {
        throw new UnsupportedOperationException();
    }
}
