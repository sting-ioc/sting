package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;

final class InjectableDescriptor
{
  @Nonnull
  private final Binding _binding;

  InjectableDescriptor( @Nonnull final Binding binding )
  {
    _binding = Objects.requireNonNull( binding );
    _binding.setOwner( this );
  }

  @Nonnull
  TypeElement getElement()
  {
    return (TypeElement) _binding.getElement();
  }

  @Nonnull
  Binding getBinding()
  {
    return _binding;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "injectable/1" );
    _binding.write( g );
    g.writeEnd();
  }
}
