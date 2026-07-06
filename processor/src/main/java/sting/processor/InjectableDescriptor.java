package sting.processor;

import java.util.Objects;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.ElementsUtil;

final class InjectableDescriptor {
    private final Binding _binding;
    /**
     * True if the java stub has been generated.
     */
    private boolean _javaStubGenerated;

    InjectableDescriptor(final Binding binding) {
        _binding = Objects.requireNonNull(binding);
        _binding.setOwner(this);
    }

    boolean isAutoDiscoverable() {
        if (1 != _binding.getPublishedServices().size()) {
            return false;
        } else {
            final Coordinate coordinate = _binding.getPublishedServices().get(0).getCoordinate();
            return coordinate.qualifier().isEmpty()
                    && coordinate.type().toString().equals(getElement().asType().toString());
        }
    }

    TypeElement getElement() {
        return ElementsUtil.getOwningType(_binding.getElement());
    }

    Binding getBinding() {
        return _binding;
    }

    boolean isJavaStubGenerated() {
        return _javaStubGenerated;
    }

    void markJavaStubAsGenerated() {
        _javaStubGenerated = true;
    }

    void write(final JsonGenerator g) {
        g.writeStartObject();
        g.write("schema", "injectable/1");
        _binding.write(g);
        g.writeEnd();
    }
}
