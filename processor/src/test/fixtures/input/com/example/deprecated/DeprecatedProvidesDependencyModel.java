package com.example.deprecated;

import sting.Fragment;

@org.jspecify.annotations.NullMarked
public interface DeprecatedProvidesDependencyModel {
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    class MyDeprecatedValue {}

    @Fragment
    interface MyFragment {
        default Runnable provideRunnable(MyDeprecatedValue value) {
            throw new UnsupportedOperationException();
        }
    }
}
