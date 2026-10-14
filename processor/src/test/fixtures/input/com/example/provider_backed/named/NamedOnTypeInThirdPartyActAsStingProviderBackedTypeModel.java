package com.example.provider_backed.named;

import sting.Named;

@org.jspecify.annotations.NullMarked
public final class NamedOnTypeInThirdPartyActAsStingProviderBackedTypeModel {
    @interface ActAsStingProvider {}

    @ActAsStingProvider
    @interface FrameworkComponent {}

    @FrameworkComponent
    @Named("value")
    static class MyComponent {}
}
