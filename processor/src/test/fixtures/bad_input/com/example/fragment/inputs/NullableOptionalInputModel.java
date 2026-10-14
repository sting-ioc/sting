package com.example.fragment.inputs;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Fragment;

@Fragment
@NullMarked
public interface NullableOptionalInputModel {
    default String provideX(@Nullable Optional<String> supplier) {
        return "";
    }
}
