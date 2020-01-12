package sting.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class Registry
{
  /**
   * The set of injectables registered.
   */
  @Nonnull
  private final Map<String, InjectableDescriptor> _injectables = new HashMap<>();
  /**
   * The set of fragments registered.
   */
  @Nonnull
  private final Map<String, FragmentDescriptor> _fragments = new HashMap<>();
  /**
   * The set of injectors registered.
   */
  @Nonnull
  private final List<InjectorDescriptor> _injectors = new ArrayList<>();

  /**
   * Register the Injectable in the local cache.
   *
   * @param injectable the injectable.
   */
  void registerInjectable( @Nonnull final InjectableDescriptor injectable )
  {
    _injectables.put( injectable.getElement().getQualifiedName().toString(), injectable );
  }

  @Nullable
  InjectableDescriptor findInjectableByClassName( @Nonnull final String name )
  {
    return _injectables.get( name );
  }

  /**
   * Register the fragment in the local cache.
   *
   * @param fragment the fragment.
   */
  void registerFragment( @Nonnull final FragmentDescriptor fragment )
  {
    _fragments.put( fragment.getElement().getQualifiedName().toString(), fragment );
  }

  @Nullable
  FragmentDescriptor findFragmentByClassName( @Nonnull final String name )
  {
    return _fragments.get( name );
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
    _injectables.clear();
    _fragments.clear();
    _injectors.clear();
  }
}
