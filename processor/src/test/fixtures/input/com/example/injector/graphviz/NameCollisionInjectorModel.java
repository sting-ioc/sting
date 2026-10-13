package com.example.injector.graphviz;

import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface NameCollisionInjectorModel {
    com.example.injector.graphviz.pkg1.MyModel getMyModel1();

    com.example.injector.graphviz.pkg2.MyModel getMyModel2();
}
