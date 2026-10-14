package com.example.injector.includes.recursive;

import sting.Injector;
import sting.Named;

@Injector(
        fragmentOnly = false,
        includes = {MyFragment1.class, MyModel1.class})
@org.jspecify.annotations.NullMarked
interface RecursiveIncludesModel {
    Runnable getRunnable1();

    @Named("Fragment2")
    Runnable getRunnable2();

    @Named("Fragment3")
    Runnable getRunnable3();

    MyModel1 getMyModel1();

    MyModel2 getMyModel2();

    MyModel3 getMyModel3();
}
