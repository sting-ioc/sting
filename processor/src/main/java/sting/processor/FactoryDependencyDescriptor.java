package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;

final class FactoryDependencyDescriptor
{
  @Nonnull
  private final ServiceRequest _serviceRequest;
  @Nonnull
  private final String _parameterName;
  @Nonnull
  private final String _fieldName;

  FactoryDependencyDescriptor( @Nonnull final ServiceRequest serviceRequest,
                               @Nonnull final String parameterName,
                               @Nonnull final String fieldName )
  {
    _serviceRequest = Objects.requireNonNull( serviceRequest );
    _parameterName = Objects.requireNonNull( parameterName );
    _fieldName = Objects.requireNonNull( fieldName );
  }

  @Nonnull
  ServiceRequest getServiceRequest()
  {
    return _serviceRequest;
  }

  @Nonnull
  String getParameterName()
  {
    return _parameterName;
  }

  @Nonnull
  String getFieldName()
  {
    return _fieldName;
  }

  boolean matches( @Nonnull final ServiceRequest serviceRequest )
  {
    final ServiceSpec existing = _serviceRequest.getService();
    final ServiceSpec candidate = serviceRequest.getService();
    return _serviceRequest.getKind() == serviceRequest.getKind() &&
           existing.isOptional() == candidate.isOptional() &&
           ServiceKey.matches( existing.getCoordinate(), candidate.getCoordinate() );
  }
}
