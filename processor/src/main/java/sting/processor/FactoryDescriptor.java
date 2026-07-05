package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

final class FactoryDescriptor {
    private final TypeElement _element;
    private final Collection<FactoryMethodDescriptor> _methods;
    private final Collection<FactoryDependencyDescriptor> _dependencies;

    private boolean _generated;

    FactoryDescriptor(
            final TypeElement element,
            final Collection<FactoryMethodDescriptor> methods,
            final Collection<FactoryDependencyDescriptor> dependencies) {
        assert ElementKind.INTERFACE == element.getKind();
        _element = Objects.requireNonNull(element);
        _methods = Objects.requireNonNull(methods);
        _dependencies = Objects.requireNonNull(dependencies);
    }

    TypeElement getElement() {
        return _element;
    }

    Collection<FactoryMethodDescriptor> getMethods() {
        return _methods;
    }

    Collection<FactoryDependencyDescriptor> getDependencies() {
        return _dependencies;
    }

    boolean isGenerated() {
        return _generated;
    }

    void markGenerated() {
        _generated = true;
    }
}
