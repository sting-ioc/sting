package com.example.multiround.injectable;

import sting.Injector;

@Injector(includes = MyFragment.class)
@org.jspecify.annotations.NullMarked
interface MyInjector {
    MyGeneratedInjectable getMyGeneratedInjectable();
}
