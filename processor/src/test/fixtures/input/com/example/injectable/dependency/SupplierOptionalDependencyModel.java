package com.example.injectable.dependency;

import java.util.Optional;
import java.util.function.Supplier;
import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class SupplierOptionalDependencyModel {
    SupplierOptionalDependencyModel(Supplier<Optional<Runnable>> runnable) {}
}
