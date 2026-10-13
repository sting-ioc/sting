package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;

@Fragment
@org.jspecify.annotations.NullMarked
public interface BasicQualifierModel {
    @Named("com.bix/SomeQualifier")
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
