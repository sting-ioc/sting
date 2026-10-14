package com.example.fragment.dependency;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface PrimitiveDependencyModel {
    default Runnable provideRunnable(boolean bool, char ch, byte b, short s, int priority, long l, float f, double d) {
        throw new UnsupportedOperationException();
    }
}
