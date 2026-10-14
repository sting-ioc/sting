package com.example.fragment.eager;

import sting.Eager;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface EagerModel {
    @Eager
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
