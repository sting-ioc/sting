package com.example.injector.includes.multiple;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MyFragment {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
