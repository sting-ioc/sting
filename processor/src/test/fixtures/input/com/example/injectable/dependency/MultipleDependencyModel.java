package com.example.injectable.dependency;

import java.util.EventListener;
import sting.Injectable;

@Injectable
@org.jspecify.annotations.NullMarked
public class MultipleDependencyModel {
    MultipleDependencyModel(Runnable runnable, EventListener listener) {}
}
