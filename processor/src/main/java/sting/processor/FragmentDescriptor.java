package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

final class FragmentDescriptor {
    /**
     * The element declaring the fragment.
     * It must be an interface.
     */
    private final TypeElement _element;
    /**
     * The list of types included by fragment.
     */
    private final Collection<IncludeDescriptor> _includes;
    /**
     * The bindings that the fragment explicitly declares.
     */
    private final Collection<Binding> _bindings;
    /**
     * True if all explicit includes must be declared in the same package as the fragment.
     */
    private final boolean _localOnly;
    /**
     * True if the java stub has been generated.
     */
    private boolean _javaStubGenerated;
    /**
     * True if the fragment has a fatal error and should not be reprocessed.
     */
    private boolean _containsError;
    /**
     * True if the fragment has a fatal error and should not be reprocessed.
     */
    private boolean _resolved;

    FragmentDescriptor(
            final TypeElement element,
            final Collection<IncludeDescriptor> includes,
            final boolean localOnly,
            final Collection<Binding> bindings) {
        assert ElementKind.INTERFACE == element.getKind();
        _element = Objects.requireNonNull(element);
        _includes = Objects.requireNonNull(includes);
        _localOnly = localOnly;
        _bindings = Objects.requireNonNull(bindings);
        _bindings.forEach(b -> b.setOwner(this));
    }

    String getQualifiedTypeName() {
        return _element.getQualifiedName().toString();
    }

    TypeElement getElement() {
        return _element;
    }

    Collection<IncludeDescriptor> getIncludes() {
        return _includes;
    }

    Collection<Binding> getBindings() {
        return _bindings;
    }

    boolean isLocalOnly() {
        return _localOnly;
    }

    boolean isJavaStubGenerated() {
        return _javaStubGenerated;
    }

    void markJavaStubAsGenerated() {
        _javaStubGenerated = true;
    }

    boolean containsError() {
        return _containsError;
    }

    void markAsContainsError() {
        _containsError = true;
    }

    boolean isResolved() {
        return _resolved;
    }

    void markAsResolved() {
        _resolved = true;
    }

    void write(final JsonGenerator g) {
        g.writeStartObject();
        g.write("schema", "fragment/1");
        if (!_includes.isEmpty()) {
            g.writeStartArray("includes");
            for (final IncludeDescriptor include : _includes) {
                g.write(include.includedType().toString());
            }
            g.writeEnd();
        }
        if (!_bindings.isEmpty()) {
            g.writeStartArray("bindings");
            for (final Binding binding : _bindings) {
                g.writeStartObject();
                binding.write(g);
                g.writeEnd();
            }
            g.writeEnd();
        }
        g.writeEnd();
    }
}
