package com.example.injector.inputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injector;

@Injector(inputs = @Injector.Input(type = Runnable.class, optional = true))
@NullMarked
interface OptionalInputInjectorModel {
    @Nullable
    Runnable getRunnable();
}
