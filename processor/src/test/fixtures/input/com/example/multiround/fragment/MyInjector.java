package com.example.multiround.fragment;

import sting.Injector;

@Injector(includes = MyFragment.class)
@org.jspecify.annotations.NullMarked
interface MyInjector {
    Object getObject();
}
