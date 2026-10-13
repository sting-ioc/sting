package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings({"CdiManagedBeanInconsistencyInspection", "Sting:Jsr330NamedPresent"})
@Injectable
@Named
@org.jspecify.annotations.NullMarked
public class SuppressedJsr330NamedInjectableModel {
    SuppressedJsr330NamedInjectableModel(int someParam) {}
}
