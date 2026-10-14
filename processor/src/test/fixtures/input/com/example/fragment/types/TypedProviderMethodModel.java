package com.example.fragment.types;

import sting.Fragment;
import sting.Typed;

@Fragment
@org.jspecify.annotations.NullMarked
public interface TypedProviderMethodModel {
    @Typed(Runnable.class)
    default MyModel provideMyModel() {
        throw new UnsupportedOperationException();
    }
}
