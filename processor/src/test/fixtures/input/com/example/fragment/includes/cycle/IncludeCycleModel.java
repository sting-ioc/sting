package com.example.fragment.includes.cycle;

import sting.Fragment;

@org.jspecify.annotations.NullMarked
public interface IncludeCycleModel {
    @Fragment(includes = B.class)
    interface A {}

    @SuppressWarnings("Sting:FragmentIncludeCycle")
    @Fragment(includes = A.class)
    interface B {}
}
