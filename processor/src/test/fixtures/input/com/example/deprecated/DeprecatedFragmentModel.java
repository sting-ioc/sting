package com.example.deprecated;

import sting.Fragment;

@Fragment
@Deprecated
@org.jspecify.annotations.NullMarked
public interface DeprecatedFragmentModel {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
