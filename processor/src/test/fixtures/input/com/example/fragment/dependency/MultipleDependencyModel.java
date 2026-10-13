package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MultipleDependencyModel {
    default Runnable provideRunnable(String name, int priority) {
        throw new UnsupportedOperationException();
    }
}
