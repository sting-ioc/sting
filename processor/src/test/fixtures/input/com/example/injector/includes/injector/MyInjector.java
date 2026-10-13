package com.example.injector.includes.injector;

import sting.Injector;

@Injector(includes = MyOtherInjectorModel.class)
@org.jspecify.annotations.NullMarked
interface MyInjector {
    MyModel getMyModel();
}
