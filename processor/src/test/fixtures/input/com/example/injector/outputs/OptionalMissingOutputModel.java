package com.example.injector.outputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.Injector;

@Injector
@NullMarked
public interface OptionalMissingOutputModel {
    @Nullable
    Runnable getRunnable();

    MyModel2 getMyModel2();

    @Injectable
    class MyModel2 {}
}
