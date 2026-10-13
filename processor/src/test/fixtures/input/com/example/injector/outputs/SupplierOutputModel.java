package com.example.injector.outputs;

import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface SupplierOutputModel {
    Supplier<MyModel> getMyModel();

    @Injectable
    class MyModel {}
}
