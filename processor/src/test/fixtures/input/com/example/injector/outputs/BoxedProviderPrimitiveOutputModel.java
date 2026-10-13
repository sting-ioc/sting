package com.example.injector.outputs;

import sting.Fragment;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface BoxedProviderPrimitiveOutputModel {
    int getValue();

    @Fragment
    interface MyFragment {
        default Integer provideValue() {
            return 42;
        }
    }
}
