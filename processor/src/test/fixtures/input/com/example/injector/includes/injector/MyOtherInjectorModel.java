package com.example.injector.includes.injector;

import sting.Injector;

@Injector(injectable = true, includes = MyFragment.class)
@org.jspecify.annotations.NullMarked
public interface MyOtherInjectorModel {
    MyModel getMyModel();
}
