package com.example.fragment.inputs;

import java.util.Collection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullableCollectionInputModel {
    default String provideX(@Nullable Collection<String> collection) {
        return null;
    }
}
