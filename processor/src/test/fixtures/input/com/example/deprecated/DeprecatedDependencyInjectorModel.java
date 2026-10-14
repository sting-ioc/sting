package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface DeprecatedDependencyInjectorModel {
    @Deprecated
    MyModel getMyModel();

    @Injectable
    class MyModel {}
}
