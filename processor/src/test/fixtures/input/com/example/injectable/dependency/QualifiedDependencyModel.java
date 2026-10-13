package com.example.injectable.dependency;

import sting.Injectable;
import sting.Named;

@Injectable
@org.jspecify.annotations.NullMarked
public class QualifiedDependencyModel {
    QualifiedDependencyModel(@Named("lively") Runnable runnable) {}
}
