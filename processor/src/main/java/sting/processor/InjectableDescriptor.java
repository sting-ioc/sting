package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;

final class InjectableDescriptor
{
  @Nonnull
  private final Binding _binding;
  /**
   * True if the java stub has been generated.
   */
  private boolean _javaStubGenerated;

  InjectableDescriptor( @Nonnull final Binding binding )
  {
    _binding = Objects.requireNonNull( binding );
    _binding.setOwner( this );
  }

  boolean isAutoDiscoverable()
  {
    if ( 1 != _binding.getPublishedServices().size() )
    {
      return false;
    }
    else
    {
      final Coordinate coordinate = _binding.getPublishedServices().get( 0 ).getCoordinate();
      return coordinate.getQualifier().isEmpty() &&
             coordinate.getType().toString().equals( getElement().asType().toString() );
    }
  }

  @Nonnull
  TypeElement getElement()
  {
    return (TypeElement) _binding.getElement().getEnclosingElement();
  }

  @Nonnull
  Binding getBinding()
  {
    return _binding;
  }

  boolean isJavaStubGenerated()
  {
    return _javaStubGenerated;
  }

  void markJavaStubAsGenerated()
  {
    _javaStubGenerated = true;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "injectable/1" );
    _binding.write( g );
    g.writeEnd();
  }
}
