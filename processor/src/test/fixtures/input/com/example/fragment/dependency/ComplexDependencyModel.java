package com.example.fragment.dependency;

import java.util.EventListener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Named;

@Fragment
@NullMarked
public interface ComplexDependencyModel {
    default Runnable provideRunnable(@Named("threadName") String name, int priority, @Nullable EventListener listener) {
        throw new UnsupportedOperationException();
    }
}
