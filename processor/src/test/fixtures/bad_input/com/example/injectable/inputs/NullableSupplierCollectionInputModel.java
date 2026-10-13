package com.example.injectable.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;

@Injectable
@NullMarked
public class NullableSupplierCollectionInputModel {
    NullableSupplierCollectionInputModel(@Nullable Collection<Supplier<String>> someValue) {}
}
