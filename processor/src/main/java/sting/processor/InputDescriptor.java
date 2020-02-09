package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;

final class InputDescriptor
{
  /**
   * The service to match.
   */
  @Nonnull
  private final ServiceSpec _service;
  /**
   * The binding that this input creates.
   */
  @Nonnull
  private final Binding _binding;
  /**
   * The name of the input in generated code.
   */
  @Nonnull
  private final String _name;

  InputDescriptor( @Nonnull final ServiceSpec service,
                   @Nonnull final Binding binding,
                   @Nonnull final String name )
  {
    _service = Objects.requireNonNull( service );
    _binding = Objects.requireNonNull( binding );
    _name = Objects.requireNonNull( name );
    _binding.setOwner( this );
  }

  @Nonnull
  String getName()
  {
    return _name;
  }

  @Nonnull
  ServiceSpec getService()
  {
    return _service;
  }

  @Nonnull
  Binding getBinding()
  {
    return _binding;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    _service.write( g );
    g.writeEnd();
  }
}
