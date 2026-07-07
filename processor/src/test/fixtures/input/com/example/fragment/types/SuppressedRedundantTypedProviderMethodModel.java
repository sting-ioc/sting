package com.example.fragment.types;

import sting.Fragment;
import sting.Typed;

@Fragment
public interface SuppressedRedundantTypedProviderMethodModel {
    @SuppressWarnings("Sting:RedundantTypedAnnotation")
    @Typed(MyModel.class)
    default MyModel provideMyModel() {
        return null;
    }
}
