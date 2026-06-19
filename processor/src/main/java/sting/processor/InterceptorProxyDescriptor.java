package sting.processor;

import com.palantir.javapoet.ClassName;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import org.realityforge.proton.GeneratorUtil;

final class InterceptorProxyDescriptor
{
  @Nonnull
  private final InterceptedServiceDescriptor _service;
  @Nonnull
  private final String _id;
  @Nonnull
  private final ClassName _className;
  private boolean _generated;

  InterceptorProxyDescriptor( @Nonnull final InterceptedServiceDescriptor service )
  {
    _service = Objects.requireNonNull( service );
    final var coordinateKey =
      service.service().getCoordinate().toString().replaceAll( "[^A-Za-z0-9]+", "_" ).replaceAll( "^_+|_+$", "" );
    _id = "proxy:" + service.binding().getId() + ":" + coordinateKey;
    final var serviceElement = (TypeElement) ( (DeclaredType) service.service().getCoordinate().type() ).asElement();
    final var ownerName = service.binding().getId().replace( '$', '_' ).replace( '.', '_' ).replace( '#', '_' );
    final var qualifierName = service.service().getCoordinate().qualifier().isEmpty() ?
                              "" :
                              "_" + service.service().getCoordinate().qualifier().replaceAll( "[^A-Za-z0-9]", "_" );
    final var simpleName =
      "Sting_" + ownerName + "_" + serviceElement.getSimpleName() + qualifierName + "_InterceptorProxy";
    _className = ClassName.get( GeneratorUtil.getQualifiedPackageName( serviceElement ), simpleName );
  }

  @Nonnull
  InterceptedServiceDescriptor getService()
  {
    return _service;
  }

  @Nonnull
  String getId()
  {
    return _id;
  }

  @Nonnull
  ClassName getClassName()
  {
    return _className;
  }

  boolean isGenerated()
  {
    return _generated;
  }

  void markGenerated()
  {
    _generated = true;
  }

  @Nonnull
  List<Binding> getGenericInterceptorBindings()
  {
    return
      _service
        .interceptors()
        .stream()
        .map( i -> i.getInterceptor().binding() )
        .collect( Collectors.toList() );
  }
}
