package com.example.deprecated;

import sting.Fragment;
import sting.Injectable;
import sting.Injector;

@Injector
@org.jspecify.annotations.NullMarked
public interface DeprecatedProvidesDependencyNodeInjectorModel {
    Runnable getRunnable();

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Injectable
    @Deprecated
    class MyOtherModel {}

    @Fragment
    interface MyFragment {
        default Runnable provideRunnable(MyOtherModel other) {
            throw new UnsupportedOperationException();
        }
    }
}
