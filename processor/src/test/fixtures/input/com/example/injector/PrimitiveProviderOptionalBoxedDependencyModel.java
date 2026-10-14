package com.example.injector;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
@NullMarked
interface PrimitiveProviderOptionalBoxedDependencyModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {
        MyModel(@Nullable final Integer value) {}
    }

    @Fragment
    interface MyFragment {
        default int provideValue() {
            return 23;
        }
    }
}
