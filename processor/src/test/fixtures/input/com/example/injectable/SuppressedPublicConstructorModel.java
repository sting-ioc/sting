package com.example.injectable;

import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class SuppressedPublicConstructorModel {
    @SuppressWarnings("Sting:PublicConstructor")
    public SuppressedPublicConstructorModel() {}
}
