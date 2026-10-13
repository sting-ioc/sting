package com.example.injectable.dependency;

import java.util.Optional;
import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class OptionalDependencyModel {
    OptionalDependencyModel(Optional<Runnable> runnable) {}
}
