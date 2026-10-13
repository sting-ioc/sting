package com.example.fragment;

import java.io.Serializable;
import java.util.EventListener;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MultiProvideModel {
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }

    default EventListener provideEventListener() {
        throw new UnsupportedOperationException();
    }

    default Serializable provideSerializable() {
        throw new UnsupportedOperationException();
    }
}
