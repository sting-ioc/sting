package com.example.injector.includes.recursive;

import sting.Fragment;

@Fragment(includes = {MyFragment2.class, MyModel2.class})
@org.jspecify.annotations.NullMarked
public interface MyFragment1 {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
