package com.example.injector;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
@NullMarked
interface MissingPrimitiveDependencyModel {
    MyModel1 getMyModel1();

    @Injectable
    class MyModel1 {
        MyModel1(@Nullable String config) {}
    }

    @Fragment
    interface MyFragment {
        // Nullable provides
        @Nullable
        default String provideConfig(int number) {
            return null;
        }
    }
}
