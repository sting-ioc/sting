package com.example.injectable.named;

import sting.Injectable;
import sting.Named;
import sting.StingProvider;
import sting.Typed;

@org.jspecify.annotations.NullMarked
public interface NamedProviderModel {
    @StingProvider("[CompoundName]Impl")
    @interface MyFrameworkComponent {}

    @MyFrameworkComponent
    class MyModel1 {
        MyModel1(@Named("") String someParam) {}
    }

    @Injectable
    @Typed(MyModel1.class)
    class MyModel1Impl extends MyModel1 {
        MyModel1Impl(@Named("") final String someParam) {
            super(someParam);
        }
    }
}
