package com.example.injector.includes.single;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
interface MyFragment {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
