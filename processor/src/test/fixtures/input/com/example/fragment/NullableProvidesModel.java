package com.example.fragment;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullableProvidesModel {
    @Nullable
    default Runnable provideRunnable() {
        return null;
    }
}
