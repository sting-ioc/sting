package com.example.injectable.dependency;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;

@Injectable
@NullMarked
public class NullableDependencyModel {
    NullableDependencyModel(@Nullable Runnable runnable) {}
}
