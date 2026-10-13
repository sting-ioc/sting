package com.example.fragment.eager;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface LazyModel {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
