package com.example.injector.outputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
@NullMarked
interface OptionalOutputModel {
    @Nullable
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
