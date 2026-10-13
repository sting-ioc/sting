package com.example.injector.inputs;

import sting.Injector;

@Injector(inputs = @Injector.Input(type = Runnable.class))
@org.jspecify.annotations.NullMarked
interface SingleInputInjectorModel {
    Runnable getRunnable();
}
