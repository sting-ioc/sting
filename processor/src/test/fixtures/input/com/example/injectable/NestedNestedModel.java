package com.example.injectable;

import sting.Injectable;

@org.jspecify.annotations.NullMarked
public class NestedNestedModel {
    public static class Middle {
        @Injectable
        public static class MyModel {}
    }
}
