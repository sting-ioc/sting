package sting.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
   * The set of auto-fragments registered.
   */
  @Nonnull
  private final Map<String, AutoFragmentDescriptor> _autoFragments = new HashMap<>();
  /**
   * The set of auto-fragments contributors registered.
   */
  @Nonnull
  private final Map<String, Set<ContributorDescriptor>> _contributors = new HashMap<>();

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

  @Nonnull
  InjectableDescriptor getInjectableByClassName( @Nonnull final String name )
  {
    return Objects.requireNonNull( findInjectableByClassName( name ) );
  }

  @Nonnull
  Collection<InjectableDescriptor> getInjectables()
  {
    return _injectables.values();
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

  @Nonnull
  FragmentDescriptor getFragmentByClassName( @Nonnull final String name )
  {
    return Objects.requireNonNull( findFragmentByClassName( name ) );
  }

  @Nonnull
  Collection<FragmentDescriptor> getFragments()
  {
    return _fragments.values();
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

  /**
   * Register the auto-fragment in the local cache.
   *
   * @param autoFragment the auto-fragment.
   */
  void registerAutoFragment( @Nonnull final AutoFragmentDescriptor autoFragment )
  {
    final String key = autoFragment.getKey();
    _autoFragments.put( key, autoFragment );
    autoFragment.markAsModified();
    autoFragment.getContributors().addAll( getContributorsByKey( key ) );
  }

  @Nullable
  AutoFragmentDescriptor findAutoFragmentByKey( @Nonnull final String key )
  {
    return _autoFragments.get( key );
  }

  @Nonnull
  Collection<AutoFragmentDescriptor> getAutoFragments()
  {
    return _autoFragments.values();
  }

  /**
   * Register the contributor in the local cache.
   *
   * @param contributor the contributor contributing to the auto-fragment.
   */
  void registerContributor( @Nonnull final ContributorDescriptor contributor )
  {
    _contributors.computeIfAbsent( contributor.getKey(), k -> new HashSet<>() ).add( contributor );
  }

  @Nonnull
  Set<ContributorDescriptor> getContributorsByKey( @Nonnull final String key )
  {
    return _contributors.getOrDefault( key, Collections.emptySet() );
  }

  @Nonnull
  Set<String> getContributorKeys()
  {
    return _contributors.keySet();
  }

  void clear()
  {
    _injectables.clear();
    _fragments.clear();
    _injectors.clear();
    _autoFragments.clear();
    _contributors.clear();
  }
}
