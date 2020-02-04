package sting.processor;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

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
  private final ServiceDescriptor[] _dependencies;
  /**
   * The service specifications published by this binding.
   */
  @Nonnull
  private final List<ServiceSpec> _publishedServices;
  /**
   * The descriptor that created the binding.
   */
  private Object _owner;

  Binding( @Nonnull final Kind kind,
           @Nonnull final String id,
           @Nonnull final List<ServiceSpec> publishedServices,
           final boolean eager,
           @Nonnull final ExecutableElement element,
           @Nonnull final ServiceDescriptor[] dependencies )
  {
    assert ( Kind.INJECTABLE == kind && ElementKind.CONSTRUCTOR == element.getKind() ) ||
           ( Kind.INJECTABLE != kind && ElementKind.METHOD == element.getKind() );
    _kind = Objects.requireNonNull( kind );
    _id = Objects.requireNonNull( id );
    _publishedServices = Objects.requireNonNull( publishedServices );
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
  List<ServiceSpec> getPublishedServices()
  {
    return _publishedServices;
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
  ServiceDescriptor[] getDependencies()
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
    if ( Kind.NULLABLE_PROVIDES == _kind )
    {
      g.write( "nullable", true );
    }
    if ( !_publishedServices.isEmpty() )
    {
      g.writeStartArray( "publishedServices" );
      for ( final ServiceSpec spec : _publishedServices )
      {
        g.writeStartObject();
        spec.getCoordinate().write( g );
        final boolean optional = spec.isOptional();
        if ( optional )
        {
          g.write( "optional", optional );
        }
        g.writeEnd();
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
      for ( final ServiceDescriptor dependency : _dependencies )
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
