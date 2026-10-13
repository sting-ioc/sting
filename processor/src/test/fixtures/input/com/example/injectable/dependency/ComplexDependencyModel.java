package com.example.injectable.dependency;

import java.io.Serializable;
import java.util.EventListener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.Named;

@Injectable
@NullMarked
public class ComplexDependencyModel {
    ComplexDependencyModel(
            @Named("lively") Runnable runnable,
            @Nullable EventListener listener,
            Serializable serializable,
            @Named("countDown") int countDown) {}
}
