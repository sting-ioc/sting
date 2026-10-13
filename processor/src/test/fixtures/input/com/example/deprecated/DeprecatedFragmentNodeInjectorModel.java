package com.example.deprecated;

import sting.Fragment;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface DeprecatedFragmentNodeInjectorModel {
    Runnable getRunnable();

    @Deprecated
    @Fragment
    interface MyFragment {
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }
}
