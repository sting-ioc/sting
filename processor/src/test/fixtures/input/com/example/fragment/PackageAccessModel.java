package com.example.fragment;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
interface PackageAccessModel {
    default Runnable provideX() {
        throw new UnsupportedOperationException();
    }
}
