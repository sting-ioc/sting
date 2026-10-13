package com.example.deprecated;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface DeprecatedProvidesModel {
    @Deprecated
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
