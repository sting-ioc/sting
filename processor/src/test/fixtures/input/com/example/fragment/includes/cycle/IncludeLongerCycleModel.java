package com.example.fragment.includes.cycle;

import sting.Fragment;

@org.jspecify.annotations.NullMarked
public interface IncludeLongerCycleModel {
    @Fragment(includes = B.class)
    interface A {}

    @Fragment(includes = C.class)
    interface B {}

    @Fragment(includes = A.class)
    interface C {}
}
