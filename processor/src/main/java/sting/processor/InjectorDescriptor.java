package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;

final class InjectorDescriptor {
    /**
     * The element that defined the Injector.
     * It may be either an interface or an abstract class.
     */
    private final TypeElement _element;
    /**
     * Should the injector be optimized for GWT.
     */
    private final boolean _gwt;
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
            final boolean gwt,
            final boolean injectable,
            final boolean fragmentOnly,
            final Collection<IncludeDescriptor> includes,
            final List<InputDescriptor> inputs,
            final List<ServiceRequest> outputs) {
        _element = Objects.requireNonNull(element);
        _gwt = gwt;
        _injectable = injectable;
        _fragmentOnly = fragmentOnly;
        _includes = Objects.requireNonNull(includes);
        _inputs = Objects.requireNonNull(inputs);
        _outputs = Objects.requireNonNull(outputs);
    }

    TypeElement getElement() {
        return _element;
    }

    boolean isGwt() {
        return _gwt;
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

    void write(final JsonGenerator g) {
        g.writeStartObject();
        g.write("schema", "injector/1");
        if (_injectable) {
            g.write("injectable", "true");
        }
        if (!_fragmentOnly) {
            g.write("fragmentOnly", "false");
        }
        if (!_includes.isEmpty()) {
            g.writeStartArray("includes");
            for (final IncludeDescriptor include : _includes) {
                g.write(include.includedType().toString());
            }
            g.writeEnd();
        }
        if (!_inputs.isEmpty()) {
            g.writeStartArray("inputs");
            for (final InputDescriptor input : _inputs) {
                g.writeStartObject();
                input.service().write(g);
                g.writeEnd();
            }
            g.writeEnd();
        }
        if (!_outputs.isEmpty()) {
            g.writeStartArray("outputs");
            for (final ServiceRequest dependency : _outputs) {
                dependency.write(g);
            }
            g.writeEnd();
        }
        g.writeEnd();
    }
}
