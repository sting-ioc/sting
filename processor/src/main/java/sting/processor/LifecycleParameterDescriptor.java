package sting.processor;

record LifecycleParameterDescriptor(Kind kind, String name) {
    enum Kind {
        SERVICE_TYPE,
        METHOD_NAME,
        BINDING_VALUE,
        ARGUMENTS,
        PROCEED,
        RESULT,
        THROWN
    }
}
