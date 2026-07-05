package sting.processor;

/**
 * @param service The service to match.
 * @param binding The binding that this input creates.
 * @param name    The name of the input in generated code.
 */
record InputDescriptor(ServiceSpec service, Binding binding, String name) {
    InputDescriptor {
        binding.setOwner(this);
    }
}
