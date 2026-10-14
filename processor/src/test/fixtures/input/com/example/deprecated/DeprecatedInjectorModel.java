package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
@Deprecated
@org.jspecify.annotations.NullMarked
public interface DeprecatedInjectorModel {
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
