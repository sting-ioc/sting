package com.example.injectable.named;

import javax.inject.Named;
import sting.Injectable;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@Injectable
@org.jspecify.annotations.NullMarked
public class Jsr330NamedInputModel {
    Jsr330NamedInputModel(@Named int someParam) {}
}
