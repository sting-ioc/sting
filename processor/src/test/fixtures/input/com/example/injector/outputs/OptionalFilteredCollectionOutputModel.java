package com.example.injector.outputs;

import java.util.Collection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
@NullMarked
interface OptionalFilteredCollectionOutputModel {
    Collection<Runnable> getRunnables();

    @Fragment
    interface MyFragment1 {
        default Runnable provideRunnable1() {
            return () -> {};
        }
    }

    @Fragment
    interface MyFragment2 {
        @Nullable
        default Runnable provideRunnable2() {
            return null;
        }
    }
}
