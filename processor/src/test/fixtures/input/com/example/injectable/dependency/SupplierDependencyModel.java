package com.example.injectable.dependency;

import java.util.function.Supplier;
import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class SupplierDependencyModel {
    SupplierDependencyModel(Supplier<Runnable> runnable) {}
}
