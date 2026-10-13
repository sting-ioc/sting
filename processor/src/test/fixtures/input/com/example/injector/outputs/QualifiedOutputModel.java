package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;
import sting.Named;

@Injector
@org.jspecify.annotations.NullMarked
interface QualifiedOutputModel {
    @Named("Foo")
    MyModel getMyModel();

    @Injectable
    @Named("Foo")
    class MyModel {}
}
