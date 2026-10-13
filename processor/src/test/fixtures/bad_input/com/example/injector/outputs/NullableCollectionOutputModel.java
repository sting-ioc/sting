package com.example.injector.outputs;

import java.util.Collection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injector;

@Injector
@NullMarked
public interface NullableCollectionOutputModel {
    @Nullable
    Collection<String> getMyThing();
}
