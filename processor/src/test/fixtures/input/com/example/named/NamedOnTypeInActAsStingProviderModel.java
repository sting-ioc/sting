package com.example.named;

import sting.ActAsStingProvider;
import sting.Named;

@org.jspecify.annotations.NullMarked
public class NamedOnTypeInActAsStingProviderModel {
    @ActAsStingProvider
    @interface MyFramework {}

    @MyFramework
    @Named("Foo")
    static class MyComponentType {}
}
