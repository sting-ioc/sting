package com.example.fragment.includes;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface Included1Model {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
