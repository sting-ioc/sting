package com.example.deprecated;

import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface DeprecatedInjectableNodeInjectorModel {
    MyModel getMyModel();

    @Deprecated
    @Injectable
    class MyModel {}
}
