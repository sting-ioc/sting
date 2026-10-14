package com.example.injectable.inputs;

import java.util.Optional;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;

@Injectable
@NullMarked
public class NullableOptionalInputModel {
    NullableOptionalInputModel(@Nullable Optional<String> someValue) {}
}
