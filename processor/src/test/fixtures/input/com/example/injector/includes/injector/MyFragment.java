package com.example.injector.includes.injector;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MyFragment {
    default MyModel provideRunnable() {
        return new MyModel();
    }
}
