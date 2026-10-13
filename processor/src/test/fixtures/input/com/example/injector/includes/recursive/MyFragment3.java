package com.example.injector.includes.recursive;

import sting.Fragment;
import sting.Named;

@Fragment
@org.jspecify.annotations.NullMarked
interface MyFragment3 {
    @Named("Fragment3")
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
