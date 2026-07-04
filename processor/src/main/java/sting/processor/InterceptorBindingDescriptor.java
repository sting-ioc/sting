package sting.processor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

final class InterceptorBindingDescriptor {
    @Nonnull
    private final AnnotationMirror _annotation;

    @Nonnull
    private final TypeElement _annotationType;

    @Nonnull
    private final Element _usageElement;

    private final int _priority;

    @Nonnull
    private final String _implementedBy;

    @Nonnull
    private final Map<String, BindingValueModel> _values;

    @Nullable
    private InterceptorDescriptor _interceptor;

    InterceptorBindingDescriptor(
            @Nonnull final AnnotationMirror annotation,
            @Nonnull final TypeElement annotationType,
            @Nonnull final Element usageElement,
            final int priority,
            @Nonnull final String implementedBy,
            @Nonnull final Map<String, BindingValueModel> values) {
        _annotation = Objects.requireNonNull(annotation);
        _annotationType = Objects.requireNonNull(annotationType);
        _usageElement = Objects.requireNonNull(usageElement);
        _priority = priority;
        _implementedBy = Objects.requireNonNull(implementedBy);
        _values = Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    @Nonnull
    AnnotationMirror getAnnotation() {
        return _annotation;
    }

    @Nonnull
    Element getUsageElement() {
        return _usageElement;
    }

    @Nonnull
    String getImplementedBy() {
        return _implementedBy;
    }

    void setInterceptor(@Nonnull final InterceptorDescriptor interceptor) {
        _interceptor = Objects.requireNonNull(interceptor);
    }

    @Nonnull
    InterceptorDescriptor getInterceptor() {
        assert null != _interceptor;
        return _interceptor;
    }

    @Nonnull
    String annotationTypeName() {
        return _annotationType.getQualifiedName().toString();
    }

    int priority() {
        return _priority;
    }

    @Nonnull
    Map<String, BindingValueModel> values() {
        return _values;
    }
}
