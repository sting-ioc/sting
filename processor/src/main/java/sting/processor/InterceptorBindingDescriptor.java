package sting.processor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.jspecify.annotations.Nullable;

final class InterceptorBindingDescriptor {
    private final AnnotationMirror _annotation;
    private final TypeElement _annotationType;
    private final Element _usageElement;

    private final int _priority;
    private final String _implementedBy;
    private final Map<String, BindingValueModel> _values;

    @Nullable
    private InterceptorDescriptor _interceptor;

    InterceptorBindingDescriptor(
            final AnnotationMirror annotation,
            final TypeElement annotationType,
            final Element usageElement,
            final int priority,
            final String implementedBy,
            final Map<String, BindingValueModel> values) {
        _annotation = Objects.requireNonNull(annotation);
        _annotationType = Objects.requireNonNull(annotationType);
        _usageElement = Objects.requireNonNull(usageElement);
        _priority = priority;
        _implementedBy = Objects.requireNonNull(implementedBy);
        _values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    AnnotationMirror getAnnotation() {
        return _annotation;
    }

    Element getUsageElement() {
        return _usageElement;
    }

    String getImplementedBy() {
        return _implementedBy;
    }

    void setInterceptor(final InterceptorDescriptor interceptor) {
        _interceptor = Objects.requireNonNull(interceptor);
    }

    InterceptorDescriptor getInterceptor() {
        return Objects.requireNonNull(_interceptor);
    }

    String annotationTypeName() {
        return _annotationType.getQualifiedName().toString();
    }

    int priority() {
        return _priority;
    }

    Map<String, BindingValueModel> values() {
        return _values;
    }
}
