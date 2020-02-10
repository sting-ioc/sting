package sting.processor;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

final class FragmentDescriptor
{
  /**
   * The element declaring the fragment.
   * It must be an interface.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * The list of types included by fragment.
   */
  @Nonnull
  private final Collection<IncludeDescriptor> _includes;
  /**
   * The bindings that the fragment explicitly declares.
   */
  @Nonnull
  private final Collection<Binding> _bindings;
  /**
   * True if the java stub has been generated.
   */
  private boolean _javaStubGenerated;
  /**
   * True if the fragment has a fatal error and should not be reprocessed.
   */
  private boolean _containsError;

  FragmentDescriptor( @Nonnull final TypeElement element,
                      @Nonnull final Collection<IncludeDescriptor> includes,
                      @Nonnull final Collection<Binding> bindings )
  {
    assert ElementKind.INTERFACE == element.getKind();
    _element = Objects.requireNonNull( element );
    _includes = Objects.requireNonNull( includes );
    _bindings = Objects.requireNonNull( bindings );
    _bindings.forEach( b -> b.setOwner( this ) );
  }

  @Nonnull
  String getQualifiedTypeName()
  {
    return _element.getQualifiedName().toString();
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  Collection<IncludeDescriptor> getIncludes()
  {
    return _includes;
  }

  @Nonnull
  Collection<Binding> getBindings()
  {
    return _bindings;
  }

  boolean isJavaStubGenerated()
  {
    return _javaStubGenerated;
  }

  void markJavaStubAsGenerated()
  {
    _javaStubGenerated = true;
  }

  boolean containsError()
  {
    return _containsError;
  }

  void markAsContainsError()
  {
    _containsError = true;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "fragment/1" );
    if ( !_includes.isEmpty() )
    {
      g.writeStartArray( "includes" );
      for ( final IncludeDescriptor include : _includes )
      {
        g.write( include.getIncludedType().toString() );
      }
      g.writeEnd();
    }
    if ( !_bindings.isEmpty() )
    {
      g.writeStartArray( "bindings" );
      for ( final Binding binding : _bindings )
      {
        g.writeStartObject();
        binding.write( g );
        g.writeEnd();
      }
      g.writeEnd();
    }
    g.writeEnd();
  }
}
