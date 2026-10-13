package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface DeprecatedProvidesNodeInjectorModel {
    Runnable getRunnable();

    @Fragment
    interface MyFragment {
        @Deprecated
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }
}
