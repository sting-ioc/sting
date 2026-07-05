package sting.processor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

record InterceptedServiceDescriptor(
        Binding binding, ServiceSpec service, List<InterceptorBindingDescriptor> interceptors) {
    InterceptedServiceDescriptor(
            final Binding binding, final ServiceSpec service, final List<InterceptorBindingDescriptor> interceptors) {
        this.binding = Objects.requireNonNull(binding);
        this.service = Objects.requireNonNull(service);
        this.interceptors = interceptors.stream()
                .sorted(Comparator.comparingInt(InterceptorBindingDescriptor::priority))
                .toList();
    }
}
