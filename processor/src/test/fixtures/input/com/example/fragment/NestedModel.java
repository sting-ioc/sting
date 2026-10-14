package com.example.fragment;

import sting.Fragment;

@org.jspecify.annotations.NullMarked
public class NestedModel {
    @Fragment
    public interface MyModel {
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }
}
