package com.example.injector;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
@NullMarked
interface NullableBoxedProviderPrimitiveOutputModel {
    int getValue();

    @Fragment
    interface MyFragment {
        @Nullable
        default Integer provideValue() {
            return null;
        }
    }
}
