package com.example.injector.outputs;

import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface BasicOutputModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
