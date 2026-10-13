package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface PrimitiveProviderBoxedOutputModel {
    Integer getValue();

    @Fragment
    interface MyFragment {
        default int provideValue() {
            return 42;
        }
    }
}
