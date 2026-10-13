package com.example.injector.includes.redundant_injectable;

import sting.Injector;

@SuppressWarnings("Sting:AutoDiscoverableIncluded")
@Injector(
        fragmentOnly = false,
        includes = {MyFragment.class, MyModel.class})
@org.jspecify.annotations.NullMarked
interface RedundantInjectableInInjectorModel {
    MyModel getMyModel();
}
