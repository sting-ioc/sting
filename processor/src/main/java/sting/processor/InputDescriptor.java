package sting.processor;

import javax.annotation.Nonnull;

/**
 * @param service The service to match.
 * @param binding The binding that this input creates.
 * @param name    The name of the input in generated code.
 */
record InputDescriptor(
        @Nonnull ServiceSpec service,
        @Nonnull Binding binding,
        @Nonnull String name) {
    InputDescriptor {
        binding.setOwner(this);
    }
}
