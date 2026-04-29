package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

final class ServiceRequest
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
   * The element that declares the service request.
   * The element will either be:
   * <ul>
   *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) of a method in a @Fragment annotated type</li>
   *   <li>a parameter (of type {@link javax.lang.model.element.VariableElement}) of the constructor in an @Injectable annotated type</li>
   *   <li>a {@link javax.lang.model.element.ExecutableElement} for a service exposed via a method on the @Injector annotated type</li>
   * </ul>
   */
  @Nonnull
  private final Element _element;
  /**
   * The index of the parameter if the service is defined by a constructor or method parameter or -1 if not.
   */
  private final int _parameterIndex;

  ServiceRequest( @Nonnull final Kind kind,
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

  boolean isOptionalLike()
  {
    return _service.isOptional() || _kind.isOptionalSingular();
  }

  boolean canBeAbsent()
  {
    return _service.isOptional() || _kind.canBeAbsent();
  }

  boolean canConsumeOptionalBindings()
  {
    return _service.isOptional() || _kind.canConsumeOptionalBindings();
  }

  void write( @Nonnull final JsonGenerator g )
  {
    g.writeStartObject();
    if ( Kind.INSTANCE != _kind )
    {
      g.write( "kind", _kind.name() );
    }
    _service.write( g );
    g.writeEnd();
  }

  enum Kind
  {
    /// A request for an instance of type T
    INSTANCE( false, false, false ),
    /// A request for an optional instance of type T. i.e. Optional<T>
    OPTIONAL( false, false, true ),
    /// A request for an instance of Supplier<T>
    SUPPLIER( true, false, false ),
    /// A request for an instance of Supplier<Optional<T>>
    SUPPLIER_OPTIONAL( true, false, true ),
    /// A request for a collection of instance of type T. i.e. Collection<T>
    COLLECTION( false, true, false ),
    /// A request for a collection of suppliers that produce instances of type T. i.e. Collection<Supplier<T>>
    SUPPLIER_COLLECTION( true, true, false ),
    /// A request for a collection of suppliers that produce optional instances of type T.
    /// i.e. Collection<Supplier<Optional<T>>>
    SUPPLIER_OPTIONAL_COLLECTION( true, true, true );
    private final boolean _supplier;
    private final boolean _collection;
    private final boolean _optional;

    Kind( final boolean supplier, final boolean collection, final boolean optional )
    {
      _supplier = supplier;
      _collection = collection;
      _optional = optional;
    }

    boolean isSupplier()
    {
      return _supplier;
    }

    boolean isCollection()
    {
      return _collection;
    }

    boolean isOptional()
    {
      return _optional;
    }

    boolean isOptionalSingular()
    {
      return _optional && !_collection;
    }

    boolean canBeAbsent()
    {
      return _collection || isOptionalSingular();
    }

    boolean canConsumeOptionalBindings()
    {
      return _optional || COLLECTION == this;
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
        return !_collection && !_supplier && !_optional ? type : null;
      }
      else
      {
        final DeclaredType declaredTypeL1 = (DeclaredType) type;
        final String classnameL1 = getClassname( declaredTypeL1 );
        final List<? extends TypeMirror> typeArgumentsL1 = declaredTypeL1.getTypeArguments();
        if ( Optional.class.getName().equals( classnameL1 ) )
        {
          return !_collection && !_supplier && _optional ?
                 extractDependencyType( typeArgumentsL1.get( 0 ) ) :
                 null;
        }
        else if ( Collection.class.getName().equals( classnameL1 ) )
        {
          if ( !_collection )
          {
            return null;
          }
          else
          {
            final TypeMirror typeL2 = typeArgumentsL1.get( 0 );
            assert TypeKind.DECLARED == typeL2.getKind();
            final DeclaredType declaredTypeL2 = (DeclaredType) typeL2;
            final String classnameL2 = getClassname( declaredTypeL2 );
            if ( Supplier.class.getName().equals( classnameL2 ) )
            {
              if ( _supplier )
              {
                final TypeMirror typeL3 = declaredTypeL2.getTypeArguments().get( 0 );
                if ( _optional )
                {
                  if ( TypeKind.DECLARED != typeL3.getKind() )
                  {
                    return null;
                  }
                  final DeclaredType declaredTypeL3 = (DeclaredType) typeL3;
                  return Optional.class.getName().equals( getClassname( declaredTypeL3 ) ) ?
                         extractDependencyType( declaredTypeL3.getTypeArguments().get( 0 ) ) :
                         null;
                }
                else
                {
                  return extractDependencyType( typeL3 );
                }
              }
              else
              {
                return null;
              }
            }
            else
            {
              return !_supplier && !_optional ? extractDependencyType( typeL2 ) : null;
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
            final TypeMirror typeL2 = typeArgumentsL1.get( 0 );
            if ( _optional )
            {
              if ( TypeKind.DECLARED == typeL2.getKind() )
              {
                final DeclaredType declaredTypeL2 = (DeclaredType) typeL2;
                return Optional.class.getName().equals( getClassname( declaredTypeL2 ) ) ?
                       extractDependencyType( declaredTypeL2.getTypeArguments().get( 0 ) ) :
                       null;
              }
              else
              {
                return null;
              }
            }
            else
            {
              return extractDependencyType( typeL2 );
            }
          }
        }
        else
        {
          return !_collection && !_supplier && !_optional ? extractDependencyType( declaredTypeL1 ) : null;
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
