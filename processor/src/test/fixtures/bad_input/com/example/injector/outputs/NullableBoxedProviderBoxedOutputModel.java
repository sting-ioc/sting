package com.example.injector.outputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injector;

@Injector
@NullMarked
interface NullableBoxedProviderBoxedOutputModel {
    Integer getValue();

    @Fragment
    interface MyFragment {
        @Nullable
        default Integer provideValue() {
            return null;
        }
    }
}
