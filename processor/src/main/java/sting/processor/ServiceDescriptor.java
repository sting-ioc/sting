package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class ServiceDescriptor
{
  /**
   * The kind of the service request.
   */
  @Nonnull
  private final Kind _kind;
  /**
   * The service to match.
   */
  @Nonnull
  private final ServiceSpec _service;
  /**
   * The element that declares the service.
   * The element will either be:
   * <ul>
   *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) of a method in a @Fragment annotated type</li>
   *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) of the constructor in an @Injectable annotated type</li>
   *   <li>a {@link javax.lang.model.element.ExecutableElement} for a service exposed via a method on the @Injector annotated type</li>
   *   <li>a {@link TypeElement} for a service declared by @Injector.inputs</li>
   * </ul>
   */
  @Nonnull
  private final Element _element;
  /**
   * The index of the parameter if the service is defined by a constructor or method parameter or -1 if not.
   */
  private final int _parameterIndex;

  ServiceDescriptor( @Nonnull final Kind kind,
                     @Nonnull final ServiceSpec service,
                     @Nonnull final Element element,
                     final int parameterIndex )
  {
    _kind = Objects.requireNonNull( kind );
    _service = Objects.requireNonNull( service );
    _element = Objects.requireNonNull( element );
    _parameterIndex = parameterIndex;
  }

  @Nonnull
  Kind getKind()
  {
    return _kind;
  }

  @Nonnull
  ServiceSpec getService()
  {
    return _service;
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
    _service.write( g );
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

    /**
     * Extract the type of value to be injected for the current kind if possible, null otherwise.
     * If this kind does not match the type supplied then return null.
     */
    @Nullable
    TypeMirror extractType( @Nonnull final TypeMirror type )
    {
      if ( TypeKind.DECLARED != type.getKind() )
      {
        return !_collection && !_supplier ? type : null;
      }
      else
      {
        final DeclaredType declaredTypeL1 = (DeclaredType) type;
        final String classnameL1 = getClassname( declaredTypeL1 );
        final List<? extends TypeMirror> typeArgumentsL1 = declaredTypeL1.getTypeArguments();
        if ( Collection.class.getName().equals( classnameL1 ) )
        {
          if ( !_collection )
          {
            return null;
          }
          else
          {
            final TypeMirror typeL2 = typeArgumentsL1.get( 0 );
            if ( TypeKind.DECLARED != typeL2.getKind() )
            {
              return !_supplier ? extractDependencyType( typeL2 ) : null;
            }
            else
            {
              final DeclaredType declaredTypeL2 = (DeclaredType) typeL2;
              final String classnameL2 = getClassname( declaredTypeL2 );
              if ( Supplier.class.getName().equals( classnameL2 ) )
              {
                return _supplier ? extractDependencyType( declaredTypeL2.getTypeArguments().get( 0 ) ) : null;
              }
              else
              {
                return extractDependencyType( typeL2 );
              }
            }
          }
        }
        else if ( Supplier.class.getName().equals( classnameL1 ) )
        {
          if ( _collection || !_supplier )
          {
            return null;
          }
          else
          {
            return extractDependencyType( typeArgumentsL1.get( 0 ) );
          }
        }
        else
        {
          return extractDependencyType( declaredTypeL1 );
        }
      }
    }

    @Nullable
    private TypeMirror extractDependencyType( @Nonnull final TypeMirror type )
    {
      return TypeKind.DECLARED == type.getKind() && !( (DeclaredType) type ).getTypeArguments().isEmpty() ? null : type;
    }

    @Nonnull
    private String getClassname( @Nonnull final DeclaredType declaredType )
    {
      return ( (TypeElement) declaredType.asElement() ).getQualifiedName().toString();
    }
  }
}
