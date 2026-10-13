package com.example.factory;

import org.jspecify.annotations.NullMarked;
import sting.Factory;
import sting.Injectable;

@NullMarked
public final class BasicFactoryModel {
    @Injectable
    public static class SomeService {}

    public static class MyComponent {
        MyComponent(final SomeService someService, final int someParameter) {}
    }

    @Factory
    public interface MyComponentFactory {
        MyComponent create(int someParameter);

        default int increment(final int value) {
            return value + 1;
        }
    }
}
