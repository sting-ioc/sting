package com.example.provider_backed.eager;

import sting.ActAsStingComponent;
import sting.Eager;

@org.jspecify.annotations.NullMarked
public final class EagerOnActAsStingComponentBackedTypeModel {
    @ActAsStingComponent
    @interface FrameworkComponent {}

    @FrameworkComponent
    @Eager
    static class MyComponent {}
}
