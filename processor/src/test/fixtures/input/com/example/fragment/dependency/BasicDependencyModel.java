package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface BasicDependencyModel {
    default Runnable provideRunnable(String name) {
        throw new UnsupportedOperationException();
    }
}
