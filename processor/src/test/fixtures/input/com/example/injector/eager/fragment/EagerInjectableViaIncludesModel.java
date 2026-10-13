package com.example.injector.eager.fragment;

import sting.Injector;

@Injector(includes = MyFragment1.class)
@org.jspecify.annotations.NullMarked
interface EagerInjectableViaIncludesModel {}
