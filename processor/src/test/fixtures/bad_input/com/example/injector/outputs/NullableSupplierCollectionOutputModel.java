package com.example.injector.outputs;

import java.util.Collection;
import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injector;

@Injector
@NullMarked
public interface NullableSupplierCollectionOutputModel {
    @Nullable
    Collection<Supplier<String>> getMyThing();
}
