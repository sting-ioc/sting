package com.example.meta;

import sting.ActAsStingConsumer;

@org.jspecify.annotations.NullMarked
public final class ActAsStingConsumerOnlyModel {
    @ActAsStingConsumer
    @interface FrameworkComponent {}

    private ActAsStingConsumerOnlyModel() {}
}
