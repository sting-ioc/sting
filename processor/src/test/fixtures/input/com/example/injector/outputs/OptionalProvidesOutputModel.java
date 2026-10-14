package com.example.injector.outputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
@NullMarked
interface OptionalProvidesOutputModel {
    @Nullable
    Runnable getRunnable();

    @Fragment
    interface MyFragment {
        @Nullable
        default Runnable provideRunnable() {
            return null;
        }
    }
}
