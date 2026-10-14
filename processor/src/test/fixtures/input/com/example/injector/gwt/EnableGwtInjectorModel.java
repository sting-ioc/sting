package com.example.injector.gwt;

import sting.Feature;
import sting.Injectable;
import sting.Injector;

@Injector(gwt = Feature.ENABLE)
@org.jspecify.annotations.NullMarked
interface EnableGwtInjectorModel {
    MyModel0 getMyModel();

    @Injectable
    class MyModel0 {}
}
