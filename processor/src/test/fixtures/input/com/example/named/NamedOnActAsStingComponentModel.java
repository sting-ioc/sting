package com.example.named;

import sting.ActAsStingComponent;
import sting.Named;

@org.jspecify.annotations.NullMarked
public class NamedOnActAsStingComponentModel {
    @ActAsStingComponent
    @interface MyFramework {}

    @MyFramework
    @Named("Foo")
    static class MyComponentType {}
}
