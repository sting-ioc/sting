package com.example.provider_backed.named;

import sting.Named;
import sting.StingProvider;

@org.jspecify.annotations.NullMarked
public final class NamedOnProviderBackedTypeModel {
    @StingProvider("[SimpleName]Impl")
    @interface FrameworkComponent {}

    @FrameworkComponent
    @Named("value")
    static class MyComponent {}
}
