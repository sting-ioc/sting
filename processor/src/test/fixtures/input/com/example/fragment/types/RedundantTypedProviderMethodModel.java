package com.example.fragment.types;

import sting.Fragment;
import sting.Typed;

@Fragment
@org.jspecify.annotations.NullMarked
public interface RedundantTypedProviderMethodModel {
    @Typed(MyModel.class)
    default MyModel provideMyModel() {
        throw new UnsupportedOperationException();
    }
}
