package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.TypeElement;

final class InjectorDescriptor {
    private final TypeElement _element;
    /**
     * Should this injector be able to be included i other injectors.
     */
    private final boolean _injectable;
    /**
     * Should explicitly included types be limited to fragments.
     */
    private final boolean _fragmentOnly;
    /**
     * The list of types included by Injector.
     */
    private final Collection<IncludeDescriptor> _includes;
    /**
     * The collection of services that must be supplied to the injector during creation.
     */
    private final List<InputDescriptor> _inputs;
    /**
     * The collection of services made available from the injector.
     */
    private final List<ServiceRequest> _outputs;
    /**
     * True if the injector has a fatal error and should not be reprocessed.
     */
    private boolean _containsError;

    InjectorDescriptor(
            final TypeElement element,
            final boolean injectable,
            final boolean fragmentOnly,
            final Collection<IncludeDescriptor> includes,
            final List<InputDescriptor> inputs,
            final List<ServiceRequest> outputs) {
        _element = Objects.requireNonNull(element);
        _injectable = injectable;
        _fragmentOnly = fragmentOnly;
        _includes = Objects.requireNonNull(includes);
        _inputs = Objects.requireNonNull(inputs);
        _outputs = Objects.requireNonNull(outputs);
    }

    TypeElement getElement() {
        return _element;
    }

    boolean isInjectable() {
        return _injectable;
    }

    boolean isFragmentOnly() {
        return _fragmentOnly;
    }

    Collection<IncludeDescriptor> getIncludes() {
        return _includes;
    }

    List<InputDescriptor> getInputs() {
        return _inputs;
    }

    List<ServiceRequest> getOutputs() {
        return _outputs;
    }

    boolean containsError() {
        return _containsError;
    }

    void markAsContainsError() {
        _containsError = true;
    }
}
