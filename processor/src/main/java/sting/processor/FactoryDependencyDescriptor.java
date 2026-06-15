package sting.processor;

import javax.annotation.Nonnull;

record FactoryDependencyDescriptor(@Nonnull ServiceRequest serviceRequest, @Nonnull String parameterName,
                                   @Nonnull String fieldName)
{
  boolean matches( @Nonnull final ServiceRequest serviceRequest )
  {
    final var existing = serviceRequest().getService();
    final var candidate = serviceRequest.getService();
    return serviceRequest().getKind() == serviceRequest.getKind() &&
           existing.isOptional() == candidate.isOptional() &&
           ServiceKey.matches( existing.getCoordinate(), candidate.getCoordinate() );
  }
}
