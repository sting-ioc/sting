package com.example.injector.outputs;

import java.util.Collection;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface EmptyCollectionOutputModel {
    Collection<Runnable> getRunnables();
}
