package com.example.factory;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Factory;
import sting.Injectable;

@NullMarked
public final class ParameterAnnotationsFactoryModel {
    @Injectable
    public static class SomeService {}

    public static class MyComponent {
        MyComponent(
                final SomeService someService, @Nullable final String name, final Runnable action, final int count) {}
    }

    @Factory
    public interface MyComponentFactory {
        MyComponent create(@Nullable String name, Runnable action, int count);
    }
}
