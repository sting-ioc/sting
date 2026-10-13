package com.example.injector.includes.provider.naming.compound;

import sting.Injector;

@Injector(
        fragmentOnly = false,
        includes = {MyModel1.class, Outer.Middle.Leaf.MyModel2.class})
@org.jspecify.annotations.NullMarked
interface MyInjector {
    MyModel1 getMyModel1();

    Outer.Middle.Leaf.MyModel2 getMyModel2();
}
