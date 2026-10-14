package com.example.meta;

import sting.StingProvider;

@org.jspecify.annotations.NullMarked
public final class StingProviderOnlyModel {
    @StingProvider("[SimpleName]Impl")
    @interface FrameworkComponent {}

    private StingProviderOnlyModel() {}
}
