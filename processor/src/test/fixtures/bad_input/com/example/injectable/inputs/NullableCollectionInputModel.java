package com.example.injectable.inputs;

import java.util.Collection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;

@Injectable
@NullMarked
public class NullableCollectionInputModel {
    NullableCollectionInputModel(@Nullable Collection<String> someValue) {}
}
