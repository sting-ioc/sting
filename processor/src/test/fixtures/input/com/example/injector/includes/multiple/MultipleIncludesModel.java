package com.example.injector.includes.multiple;

import sting.Injector;

@Injector(
        fragmentOnly = false,
        includes = {MyFragment.class, MyModel.class})
@org.jspecify.annotations.NullMarked
interface MultipleIncludesModel {
    Runnable getRunnable();
}
