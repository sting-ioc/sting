package sting.processor;

record FactoryDependencyDescriptor(ServiceRequest serviceRequest, String parameterName, String fieldName) {
    boolean matches(final ServiceRequest serviceRequest) {
        final var existing = serviceRequest().getService();
        final var candidate = serviceRequest.getService();
        return serviceRequest().getKind() == serviceRequest.getKind()
                && existing.isOptional() == candidate.isOptional()
                && ServiceKey.matches(existing.getCoordinate(), candidate.getCoordinate());
    }
}
