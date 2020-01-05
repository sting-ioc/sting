package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

final class Binding
{
  /**
   * The type of the binding.
   */
  @Nonnull
  private final Type _bindingType;
  /**
   * An opaque string used to restrict the requests that match this binding.
   */
  @Nonnull
  private final String _qualifier;
  /**
   * The types that are exposed via this binding.
   */
  @Nonnull
  private final TypeMirror[] _types;
  /**
   * Is the binding eager or lazy. Eager bindings are instantiated after the injector is instantiated
   * and before it is made accessible to user-code.
   */
  private final boolean _eager;
  /**
   * The element that created this binding.
   * The field will be a {@link javax.lang.model.element.TypeElement} for an {@link Type#INJECTABLE} binding
   * otherwise it will be an {@link javax.lang.model.element.ExecutableElement} for a {@link Type#PROVIDES} binding
   * or a a {@link Type#NULLABLE_PROVIDES} binding.
   */
  @Nonnull
  private final Element _element;
  /**
   * The dependencies that need to be supplied when creating the value.
   */
  @Nonnull
  private final DependencyDescriptor[] _dependencies;

  Binding( @Nonnull final Type bindingType,
           @Nonnull final String qualifier,
           @Nonnull final TypeMirror[] types,
           final boolean eager,
           @Nonnull final Element element,
           @Nonnull final DependencyDescriptor[] dependencies )
  {
    _bindingType = Objects.requireNonNull( bindingType );
    _qualifier = Objects.requireNonNull( qualifier );
    _types = Objects.requireNonNull( types );
    _eager = eager;
    _element = Objects.requireNonNull( element );
    _dependencies = Objects.requireNonNull( dependencies );
  }

  @Nonnull
  Type getBindingType()
  {
    return _bindingType;
  }

  @Nonnull
  String getQualifier()
  {
    return _qualifier;
  }

  @Nonnull
  TypeMirror[] getTypes()
  {
    return _types;
  }

  boolean isEager()
  {
    return _eager;
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }

  @Nonnull
  DependencyDescriptor[] getDependencies()
  {
    return _dependencies;
  }

  void emitBindingJson( @Nonnull final JsonGenerator g )
  {
    if ( Type.INJECTABLE != _bindingType )
    {
      g.write( "providesMethod", _element.getSimpleName().toString() );
    }
    if ( !_qualifier.isEmpty() )
    {
      g.write( "qualifier", _qualifier );
    }
    if ( Type.NULLABLE_PROVIDES == _bindingType )
    {
      g.write( "nullable", true );
    }
    if ( _types.length > 0 )
    {
      g.writeStartArray( "types" );
      for ( final TypeMirror type : _types )
      {
        g.write( type.toString() );
      }
      g.writeEnd();
    }
    if ( _eager )
    {
      g.write( "eager", _eager );
    }
    if ( _dependencies.length > 0 )
    {
      g.writeStartArray( "dependencies" );
      for ( final DependencyDescriptor dependency : _dependencies )
      {
        g.writeStartObject();
        final DependencyDescriptor.Type type = dependency.getType();
        if ( DependencyDescriptor.Type.INSTANCE != type )
        {
          g.write( "type", type.toString() );
        }

        g.writeStartObject( "coordinate" );
        final Coordinate coordinate = dependency.getCoordinate();
        final String qualifier = coordinate.getQualifier();
        if ( !qualifier.isEmpty() )
        {
          g.write( "qualifier", qualifier );
        }
        g.write( "type", coordinate.getType().toString() );
        g.writeEnd();
        final boolean nullable = dependency.isOptional();
        if ( nullable )
        {
          g.write( "nullable", nullable );
        }
        g.writeEnd();
      }
      g.writeEnd();
    }
  }

  enum Type
  {
    /// Instances are created by invoking the constructor
    INJECTABLE,
    /// Instances are bound by invoking @Provides annotated method
    PROVIDES,
    /// Instances are bound by invoking @Provides annotated method that is also annotated by @Nullable
    NULLABLE_PROVIDES
  }
}
