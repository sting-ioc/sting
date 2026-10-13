package com.example.fragment.inputs;

import java.util.Collection;
import java.util.function.Supplier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullableSupplierCollectionInputModel {
    default String provideX(@Nullable Collection<Supplier<String>> collection) {
        return null;
    }
}
