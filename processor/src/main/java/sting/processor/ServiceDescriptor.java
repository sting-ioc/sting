package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.ElementsUtil;

final class ServiceDescriptor
{
  /**
   * The kind of the service request.
   */
  @Nonnull
  private final Kind _kind;
  /**
   * The coordinate of the service to match.
   */
  @Nonnull
  private final Coordinate _coordinate;
  /**
   * Is the service optional.
   */
  private final boolean _optional;
  /**
   * The element that declares the service.
   * The element will either be:
    *<ul>
    *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) on a @Provides annotated method</li>
    *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) of the constructor in an @Injectable annotated type</li>
    *   <li>a {@link javax.lang.model.element.ExecutableElement} for a service exposed via a method on the @Injector annotated type</li>
    *   <li>a {@link TypeElement} for a service declared by @Injector.inputs</li>
    *</ul>
   */
  @Nonnull
  private final Element _element;
  /**
   * The index of the parameter if the service is defined by a constructor or method parameter or -1 if not.
   */
  private final int _parameterIndex;

  ServiceDescriptor( @Nonnull final Kind kind,
                     @Nonnull final Coordinate coordinate,
                     final boolean optional,
                     @Nonnull final Element element,
                     final int parameterIndex )
  {
    _kind = Objects.requireNonNull( kind );
    _coordinate = Objects.requireNonNull( coordinate );
    _optional = optional;
    _element = Objects.requireNonNull( element );
    _parameterIndex = parameterIndex;
  }

  @Nonnull
  Kind getKind()
  {
    return _kind;
  }

  @Nonnull
  Coordinate getCoordinate()
  {
    return _coordinate;
  }

  boolean isOptional()
  {
    return _optional;
  }

  boolean isPublic()
  {
    final TypeMirror type = _coordinate.getType();
    return
      TypeKind.DECLARED != type.getKind() ||
      ElementsUtil.isEffectivelyPublic( (TypeElement) ( (DeclaredType) type ).asElement() );
  }

  @Nonnull
  Element getElement()
  {
    return _element;
  }

  int getParameterIndex()
  {
    return _parameterIndex;
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    if ( Kind.INSTANCE != _kind )
    {
      g.write( "type", _kind.name() );
    }

    _coordinate.write( g );
    if ( _optional )
    {
      g.write( "optional", true );
    }
    g.writeEnd();
  }

  enum Kind
  {
    /// A request for an instance of type T
    INSTANCE( false, false ),
    /// A request for an instance of Supplier<T>
    SUPPLIER( true, false ),
    /// A request for a collection of instance of type T. i.e. Collection<T>
    COLLECTION( false, true ),
    /// A request for a collection of suppliers that produce instances of type T. i.e. Collection<Supplier<T>>
    SUPPLIER_COLLECTION( true, true );
    private final boolean _supplier;
    private final boolean _collection;

    Kind( final boolean supplier, final boolean collection )
    {
      _supplier = supplier;
      _collection = collection;
    }

    boolean isSupplier()
    {
      return _supplier;
    }

    boolean isCollection()
    {
      return _collection;
    }
  }
}
