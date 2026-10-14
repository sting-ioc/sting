package com.example.bad_descriptors.scenario3;

import sting.Injector;

@Injector(includes = MyFragment.class)
@org.jspecify.annotations.NullMarked
interface MyInjectorModel {
    Model2 getModel2();
}
