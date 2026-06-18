package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import sting.processor.spi.BindingValueModel;
import sting.processor.spi.InterceptorBindingModel;

final class InterceptorBindingDescriptor
  implements InterceptorBindingModel
{
  enum ClaimState
  {
    UNCLAIMED,
    CLAIMED,
    CONFLICT
  }

  @Nonnull
  private final ServiceSpec _service;
  @Nonnull
  private final AnnotationMirror _annotation;
  @Nonnull
  private final TypeElement _annotationType;
  @Nonnull
  private final Element _usageElement;
  private final int _priority;
  @Nonnull
  private final String _implementedBy;
  @Nonnull
  private final Map<String, BindingValueModelImpl> _values;
  @Nonnull
  private ClaimState _claimState = ClaimState.UNCLAIMED;
  @Nonnull
  private final List<String> _pluginIds = new ArrayList<>();
  @Nullable
  private InterceptorDescriptor _interceptor;

  InterceptorBindingDescriptor( @Nonnull final ServiceSpec service,
                                @Nonnull final AnnotationMirror annotation,
                                @Nonnull final TypeElement annotationType,
                                @Nonnull final Element usageElement,
                                final int priority,
                                @Nonnull final String implementedBy,
                                @Nonnull final Map<String, BindingValueModelImpl> values )
  {
    _service = Objects.requireNonNull( service );
    _annotation = Objects.requireNonNull( annotation );
    _annotationType = Objects.requireNonNull( annotationType );
    _usageElement = Objects.requireNonNull( usageElement );
    _priority = priority;
    _implementedBy = Objects.requireNonNull( implementedBy );
    _values = Collections.unmodifiableMap( new LinkedHashMap<>( values ) );
  }

  @Nonnull
  ServiceSpec getService()
  {
    return _service;
  }

  @Nonnull
  AnnotationMirror getAnnotation()
  {
    return _annotation;
  }

  @Nonnull
  Element getUsageElement()
  {
    return _usageElement;
  }

  @Nonnull
  String getImplementedBy()
  {
    return _implementedBy;
  }

  boolean hasGenericInterceptor()
  {
    return ClaimState.CLAIMED != _claimState;
  }

  @Nonnull
  ClaimState getClaimState()
  {
    return _claimState;
  }

  void setClaimedBy( @Nonnull final String pluginId )
  {
    assert ClaimState.UNCLAIMED == _claimState;
    _claimState = ClaimState.CLAIMED;
    _pluginIds.add( pluginId );
  }

  void setConflict( @Nonnull final List<String> pluginIds )
  {
    _claimState = ClaimState.CONFLICT;
    _pluginIds.clear();
    _pluginIds.addAll( pluginIds );
    Collections.sort( _pluginIds );
  }

  @Nonnull
  List<String> getPluginIds()
  {
    return _pluginIds;
  }

  void setInterceptor( @Nonnull final InterceptorDescriptor interceptor )
  {
    _interceptor = Objects.requireNonNull( interceptor );
  }

  @Nonnull
  InterceptorDescriptor getInterceptor()
  {
    assert null != _interceptor;
    return _interceptor;
  }

  @Nonnull
  @Override
  public String annotationTypeName()
  {
    return _annotationType.getQualifiedName().toString();
  }

  @Override
  public int priority()
  {
    return _priority;
  }

  @Nonnull
  @Override
  public String serviceTypeName()
  {
    return _service.getCoordinate().type().toString();
  }

  @Nonnull
  @Override
  public String qualifierKey()
  {
    return _service.getCoordinate().qualifier();
  }

  @Nonnull
  @Override
  public Set<String> valueNames()
  {
    return _values.keySet();
  }

  @Nonnull
  @Override
  public BindingValueModel value( @Nonnull final String name )
  {
    final BindingValueModel value = _values.get( name );
    if ( null == value )
    {
      throw new IllegalArgumentException( "No interceptor binding value named " + name );
    }
    return value;
  }

  @Nonnull
  Map<String, BindingValueModelImpl> values()
  {
    return _values;
  }
}
