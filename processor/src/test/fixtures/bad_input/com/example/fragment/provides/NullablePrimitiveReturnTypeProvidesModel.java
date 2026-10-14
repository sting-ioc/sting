package com.example.fragment.provides;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullablePrimitiveReturnTypeProvidesModel {
    @SuppressWarnings("NullableProblems")
    @Nullable
    default int provideX() {
        return 0;
    }
}
