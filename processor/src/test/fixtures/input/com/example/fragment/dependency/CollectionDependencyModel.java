package com.example.fragment.dependency;

import java.util.Collection;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface CollectionDependencyModel {
    default Runnable provideRunnable(Collection<String> name) {
        throw new UnsupportedOperationException();
    }
}
