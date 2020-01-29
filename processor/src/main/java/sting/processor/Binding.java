package sting.processor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

final class Binding
{
  /**
   * The kind of the binding.
   */
  @Nonnull
  private final Kind _kind;
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
   * The field will be a constructor for an {@link Kind#INJECTABLE} binding
   * otherwise it will be a method for a {@link Kind#PROVIDES} binding
   * or a {@link Kind#NULLABLE_PROVIDES} binding.
   */
  @Nonnull
  private final ExecutableElement _element;
  /**
   * The dependencies that need to be supplied when creating a binding instance.
   */
  @Nonnull
  private final DependencyDescriptor[] _dependencies;
  /**
   * The descriptor that created the binding.
   */
  private Object _owner;

  Binding( @Nonnull final Kind kind,
           @Nonnull final String id,
           @Nonnull final String qualifier,
           @Nonnull final TypeMirror[] types,
           final boolean eager,
           @Nonnull final ExecutableElement element,
           @Nonnull final DependencyDescriptor[] dependencies )
  {
    assert ( Kind.INJECTABLE == kind && ElementKind.CONSTRUCTOR == element.getKind() ) ||
           ( Kind.INJECTABLE != kind && ElementKind.METHOD == element.getKind() );
    _kind = Objects.requireNonNull( kind );
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
  Kind getKind()
  {
    return _kind;
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
  ExecutableElement getElement()
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
    if ( Kind.INJECTABLE != _kind )
    {
      g.write( "providesMethod", _element.getSimpleName().toString() );
    }
    if ( !_qualifier.isEmpty() )
    {
      g.write( "qualifier", _qualifier );
    }
    if ( Kind.NULLABLE_PROVIDES == _kind )
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
    assert ( owner instanceof InjectableDescriptor && Kind.INJECTABLE == _kind ) ||
           ( owner instanceof FragmentDescriptor && Kind.PROVIDES == _kind ) ||
           ( owner instanceof FragmentDescriptor && Kind.NULLABLE_PROVIDES == _kind );
    _owner = owner;
  }

  @Nonnull
  String describe()
  {
    final String className = ( (TypeElement) _element.getEnclosingElement() ).getQualifiedName().toString();
    if ( Kind.INJECTABLE == _kind )
    {
      return className;
    }
    else
    {
      assert Kind.PROVIDES == _kind || Kind.NULLABLE_PROVIDES == _kind;
      return className + "." + _element.getSimpleName();
    }
  }

  @Nonnull
  String getTypeLabel()
  {
    if ( Kind.INJECTABLE == _kind )
    {
      return "[Injectable] ";
    }
    else
    {
      assert Kind.PROVIDES == _kind || Kind.NULLABLE_PROVIDES == _kind;
      return "[Provides]   ";
    }
  }

  enum Kind
  {
    /// Instances are created by invoking the constructor
    INJECTABLE,
    /// Instances are bound by invoking @Provides annotated method
    PROVIDES,
    /// Instances are bound by invoking @Provides annotated method that is also annotated by @Nullable
    NULLABLE_PROVIDES
  }
}
