package com.example.fragment.types;

import sting.Eager;
import sting.Fragment;
import sting.Typed;

@Fragment
@org.jspecify.annotations.NullMarked
public interface NoTypesModel {
    @Eager
    @Typed({})
    default String provideConfig() {
        throw new UnsupportedOperationException();
    }
}
