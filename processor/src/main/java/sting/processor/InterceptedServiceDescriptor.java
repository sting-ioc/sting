package sting.processor;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

record InterceptedServiceDescriptor(@Nonnull Binding binding, @Nonnull ServiceSpec service,
                                    @Nonnull List<InterceptorBindingDescriptor> interceptors)
{
  InterceptedServiceDescriptor( @Nonnull final Binding binding,
                                @Nonnull final ServiceSpec service,
                                @Nonnull final List<InterceptorBindingDescriptor> interceptors )
  {
    this.binding = Objects.requireNonNull( binding );
    this.service = Objects.requireNonNull( service );
    this.interceptors = interceptors
      .stream()
      .sorted( Comparator.comparingInt( InterceptorBindingDescriptor::priority ) )
      .toList();
  }
}
