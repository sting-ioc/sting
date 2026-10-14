package com.example.bad_descriptors.scenario3;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MyFragment {
    default Model2 provideModel2(Model1 model) {
        return new Model2(model);
    }
}
