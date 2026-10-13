package com.example.multistage.stage1;

import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface MyFragment {
    default Model2 provideModel2() {
        return new Model2();
    }
}
