package com.example.named;

import sting.InjectorFragment;
import sting.Named;

@InjectorFragment
@org.jspecify.annotations.NullMarked
interface NamedOnInjectorFragment {
    @Named("Foo")
    String getConfig();
}
