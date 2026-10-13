package com.example.provider_backed.eager;

import sting.Eager;
import sting.StingProvider;

@org.jspecify.annotations.NullMarked
public final class EagerOnProviderBackedTypeModel {
    @StingProvider("[SimpleName]Impl")
    @interface FrameworkComponent {}

    @FrameworkComponent
    @Eager
    static class MyComponent {}
}
