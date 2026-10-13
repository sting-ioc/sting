package com.example.provider_backed.named;

import sting.ActAsStingConsumer;
import sting.Named;

@org.jspecify.annotations.NullMarked
public final class NamedOnCtorParamInActAsStingConsumerBackedTypeModel {
    @ActAsStingConsumer
    @interface FrameworkComponent {}

    @FrameworkComponent
    static class MyComponent {
        MyComponent(@Named("value") final String value) {}
    }
}
