package com.example.injector.inputs;

import sting.Injectable;
import sting.Injector;

@Injector(inputs = @Injector.Input(type = int.class))
@org.jspecify.annotations.NullMarked
interface PrimitiveInputBoxedDependencyInjectorModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {
        MyModel(final Integer value) {}
    }
}
