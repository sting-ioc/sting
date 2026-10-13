package com.example.factory;

import org.jspecify.annotations.NullMarked;
import sting.Factory;
import sting.Injectable;

@NullMarked
public final class MultiMethodFactoryModel {
    @Injectable
    public static class SomeService {}

    @Injectable
    public static class OtherService {}

    public static class Widget {
        Widget(final SomeService someService, final int count) {}
    }

    public static class Gadget {
        Gadget(final SomeService someService, final OtherService otherService, final String name) {}
    }

    @Factory
    public interface ModelFactory {
        Widget createWidget(int count);

        Gadget createGadget(final String name);
    }
}
