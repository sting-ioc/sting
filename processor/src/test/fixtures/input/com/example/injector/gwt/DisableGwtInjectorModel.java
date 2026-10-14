package com.example.injector.gwt;

import sting.Feature;
import sting.Injectable;
import sting.Injector;

@Injector(gwt = Feature.DISABLE)
@org.jspecify.annotations.NullMarked
interface DisableGwtInjectorModel {
    MyModel0 getMyModel();

    @Injectable
    class MyModel0 {}
}
