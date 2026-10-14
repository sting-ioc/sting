package com.example.meta;

import sting.ActAsStingComponent;

@org.jspecify.annotations.NullMarked
public final class ActAsStingComponentOnlyModel {
    @ActAsStingComponent
    @interface FrameworkComponent {}

    private ActAsStingComponentOnlyModel() {}
}
