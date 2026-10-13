package com.example.injector.includes.provider.naming.enclosing;

import sting.Injector;

@Injector(
        fragmentOnly = false,
        includes = {MyModel.class, Outer.Middle.Leaf.MyModel2.class})
@org.jspecify.annotations.NullMarked
interface MyInjector {
    MyModel getMyModel();

    Outer.Middle.Leaf.MyModel2 getMyModel2();
}
