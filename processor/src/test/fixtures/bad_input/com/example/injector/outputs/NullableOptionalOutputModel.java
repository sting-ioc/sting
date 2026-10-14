package com.example.injector.outputs;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injector;

@Injector
@NullMarked
public interface NullableOptionalOutputModel {
    @Nullable
    Optional<String> getValue();
}
