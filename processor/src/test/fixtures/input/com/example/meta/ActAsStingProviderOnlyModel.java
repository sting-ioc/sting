package com.example.meta;

import sting.ActAsStingProvider;

@org.jspecify.annotations.NullMarked
public final class ActAsStingProviderOnlyModel {
    @ActAsStingProvider
    @interface FrameworkComponent {}

    private ActAsStingProviderOnlyModel() {}
}
