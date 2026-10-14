package com.example.fragment;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface BasicModel {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
