package com.example.fragment.dependency;

import sting.Fragment;
import sting.Named;

@Fragment
@org.jspecify.annotations.NullMarked
public interface QualifiedDependencyModel {
    default Runnable provideRunnable(@Named("threadName") String name) {
        throw new UnsupportedOperationException();
    }
}
