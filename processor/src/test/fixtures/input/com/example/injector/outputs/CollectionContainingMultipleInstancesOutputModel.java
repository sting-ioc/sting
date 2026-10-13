package com.example.injector.outputs;

import java.util.Collection;
import sting.Fragment;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface CollectionContainingMultipleInstancesOutputModel {
    Collection<Runnable> getRunnables();

    @Fragment
    interface MyFragment1 {
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }

    @Fragment
    interface MyFragment2 {
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }

    @Fragment
    interface MyFragment3 {
        default Runnable provideRunnable() {
            throw new UnsupportedOperationException();
        }
    }
}
