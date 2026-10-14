package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
interface SupplierCollectionOutputModel {
    Collection<Supplier<MyModel>> getMyModel();

    @Injectable
    class MyModel {}
}
