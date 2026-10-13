package sting.doc.examples.inputs;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injector;

@Injector(
        inputs = {
            @Injector.Input(type = AuthContext.class),
            @Injector.Input(type = LocalStorage.class, optional = true),
            @Injector.Input(type = String.class, qualifier = "hostname", optional = true)
        })
@NullMarked
public interface MyInjector {
    static MyInjector create(
            final AuthContext authContext, @Nullable final LocalStorage localStorage, @Nullable final String hostname) {
        return new Sting_MyInjector(authContext, localStorage, hostname);
    }
    // DOC ELIDE START
    // DOC ELIDE END
}
