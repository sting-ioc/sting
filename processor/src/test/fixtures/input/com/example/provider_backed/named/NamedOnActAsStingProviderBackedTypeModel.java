package com.example.provider_backed.named;

import sting.ActAsStingProvider;
import sting.Named;

@org.jspecify.annotations.NullMarked
public final class NamedOnActAsStingProviderBackedTypeModel {
    @ActAsStingProvider
    @interface FrameworkComponent {}

    @FrameworkComponent
    @Named("value")
    static class MyComponent {}
}
