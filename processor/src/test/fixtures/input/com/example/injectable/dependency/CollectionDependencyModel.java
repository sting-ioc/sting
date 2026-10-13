package com.example.injectable.dependency;

import java.util.Collection;
import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class CollectionDependencyModel {
    CollectionDependencyModel(Collection<Runnable> runnable) {}
}
