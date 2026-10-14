package com.example.injector.includes.single;

import sting.Injector;

@Injector(includes = MyFragment.class)
@org.jspecify.annotations.NullMarked
interface SingleIncludesModel {
    Runnable getRunnable();
}
