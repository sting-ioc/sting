package com.example.injectable;

import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class SuppressedProtectedConstructorModel {
    @SuppressWarnings("Sting:ProtectedConstructor")
    protected SuppressedProtectedConstructorModel() {}
}
