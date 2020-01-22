package sting.processor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class Binding
{
  /**
   * The type of the binding.
   */
  @Nonnull
  private final Type _bindingType;
  /**
   * A unique identifier for the binding which can be specified by the developer or derived automatically.
   * For an INJECTABLE binding this is the fully qualified name of the class. For other bindings it is
   * the fully qualified name of the class combined with the name of the method.
   */
  @Nonnull
  private final String _id;
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
   * The coordinates for binding.
   */
  @Nonnull
  private final List<Coordinate> _coordinates;
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
  /**
   * The descriptor that created the binding.
   */
  private Object _owner;

  Binding( @Nonnull final Type bindingType,
           @Nonnull final String id,
           @Nonnull final String qualifier,
           @Nonnull final TypeMirror[] types,
           final boolean eager,
           @Nonnull final Element element,
           @Nonnull final DependencyDescriptor[] dependencies )
  {
    assert ( Type.INJECTABLE == bindingType && ElementKind.CLASS == element.getKind() ) ||
           ( Type.INJECTABLE != bindingType && ElementKind.METHOD == element.getKind() );
    _bindingType = Objects.requireNonNull( bindingType );
    _id = Objects.requireNonNull( id );
    _qualifier = Objects.requireNonNull( qualifier );
    _types = Objects.requireNonNull( types );
    _coordinates = Stream.of( _types ).map( t -> new Coordinate( qualifier, t ) ).collect( Collectors.toList() );
    _eager = eager;
    _element = Objects.requireNonNull( element );
    _dependencies = Objects.requireNonNull( dependencies );
  }

  @Nonnull
  Object getOwner()
  {
    assert null != _owner;
    return _owner;
  }

  @Nonnull
  Type getBindingType()
  {
    return _bindingType;
  }

  @Nonnull
  String getId()
  {
    return _id;
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

  @Nonnull
  List<Coordinate> getCoordinates()
  {
    return _coordinates;
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

  void write( @Nonnull final JsonGenerator g )
  {
    g.write( "id", _id );
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
        dependency.write( g );
      }
      g.writeEnd();
    }
  }

  void setOwner( @Nonnull final Object owner )
  {
    assert null == _owner;
    assert ( owner instanceof InjectableDescriptor && Type.INJECTABLE == _bindingType ) ||
           ( owner instanceof FragmentDescriptor && Type.PROVIDES == _bindingType ) ||
           ( owner instanceof FragmentDescriptor && Type.NULLABLE_PROVIDES == _bindingType );
    _owner = owner;
  }

  @Nonnull
  String describe()
  {
    if ( Binding.Type.INJECTABLE == _bindingType )
    {
      return ( (TypeElement) _element ).getQualifiedName().toString();
    }
    else
    {
      assert Binding.Type.PROVIDES == _bindingType || Binding.Type.NULLABLE_PROVIDES == _bindingType;
      return ( (TypeElement) _element.getEnclosingElement() ).getQualifiedName() + "." + _element.getSimpleName();
    }
  }

  @Nonnull
  String getTypeLabel()
  {
    if ( Binding.Type.INJECTABLE == _bindingType )
    {
      return "[Injectable] ";
    }
    else
    {
      assert Binding.Type.PROVIDES == _bindingType || Binding.Type.NULLABLE_PROVIDES == _bindingType;
      return "[Provides]   ";
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
