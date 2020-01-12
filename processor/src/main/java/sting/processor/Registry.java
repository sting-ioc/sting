package sting.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.lang.model.type.TypeMirror;

final class Registry
{
  /**
   * The set of bindings registered.
   */
  @Nonnull
  private final List<Binding> _bindings = new ArrayList<>();
  /**
   * The set of fragments registered.
   */
  @Nonnull
  private final List<FragmentDescriptor> _fragments = new ArrayList<>();
  /**
   * The set of injectors registered.
   */
  @Nonnull
  private final List<InjectorDescriptor> _injectors = new ArrayList<>();
  /**
   * The published types of bindings that have been registered.
   */
  @Nonnull
  private final Map<Coordinate, List<Binding>> _publishedTypes = new LinkedHashMap<>();

  /**
   * Register the binding in the local cache.
   *
   * @param binding the binding.
   */
  void registerBinding( @Nonnull final Binding binding )
  {
    _bindings.add( binding );
    for ( final TypeMirror publishedType : binding.getTypes() )
    {
      final Coordinate key = new Coordinate( binding.getQualifier(), publishedType );
      _publishedTypes.computeIfAbsent( key, c -> new ArrayList<>() ).add( binding );
    }
  }

  /**
   * Register the fragment in the local cache.
   *
   * @param fragment the fragment.
   */
  void registerFragment( @Nonnull final FragmentDescriptor fragment )
  {
    _fragments.add( fragment );
    for ( final Binding binding : fragment.getBindings() )
    {
      registerBinding( binding );
    }
  }

  /**
   * Register the injector in the local cache.
   *
   * @param injector the injector.
   */
  void registerInjector( @Nonnull final InjectorDescriptor injector )
  {
    _injectors.add( injector );
  }

  void deregisterInjector( @Nonnull final InjectorDescriptor injector )
  {
    _injectors.remove( injector );
  }

  @Nonnull
  List<InjectorDescriptor> getInjectors()
  {
    return _injectors;
  }

  void clear()
  {
    _bindings.clear();
    _fragments.clear();
    _injectors.clear();
    _publishedTypes.clear();
  }
}
