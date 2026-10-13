package com.example.injectable.dependency;

import org.jspecify.annotations.NullMarked;
import sting.Injectable;

@Injectable
@NullMarked
public class NonnullDependencyModel {
    NonnullDependencyModel(Runnable runnable) {}
}
