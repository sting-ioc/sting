package com.example.injector;

import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface BasicInjectorModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
