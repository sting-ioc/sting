package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.type.TypeMirror;

final class ObjectGraph
{
  /**
   * The injector that defines the graph.
   */
  @Nonnull
  private final InjectorDescriptor _injector;
  /**
   * The set of injectables explicitly included in the graph.
   */
  @Nonnull
  private final Map<String, InjectableDescriptor> _injectables = new HashMap<>();
  /**
   * The set of fragments explicitly included in the graph.
   */
  @Nonnull
  private final Map<String, FragmentDescriptor> _fragments = new HashMap<>();
  /**
   * The set of bindings included in the graph derived from descriptors.
   */
  @Nonnull
  private final List<Binding> _bindings = new ArrayList<>();
  /**
   * The types that are published in the object graph.
   */
  @Nonnull
  private final Map<Coordinate, List<Binding>> _publishedTypes = new LinkedHashMap<>();

  ObjectGraph( @Nonnull final InjectorDescriptor injector )
  {
    _injector = Objects.requireNonNull( injector );
  }

  @Nonnull
  InjectorDescriptor getInjector()
  {
    return _injector;
  }

  /**
   * Register the binding in the object graph.
   *
   * @param binding the binding.
   */
  private void registerBinding( @Nonnull final Binding binding )
  {
    _bindings.add( binding );
    for ( final TypeMirror publishedType : binding.getTypes() )
    {
      final Coordinate key = new Coordinate( binding.getQualifier(), publishedType );
      _publishedTypes.computeIfAbsent( key, c -> new ArrayList<>() ).add( binding );
    }
  }

  @Nonnull
  List<Binding> findAllBindingsByCoordinate( @Nonnull final Coordinate coordinate )
  {
    return _publishedTypes.getOrDefault( coordinate, Collections.emptyList() );
  }

  /**
   * Include the injectable in the object graph.
   *
   * @param injectable the injectable.
   */
  void registerInjectable( @Nonnull final InjectableDescriptor injectable )
  {
    _injectables.put( injectable.getElement().getQualifiedName().toString(), injectable );
    registerBinding( injectable.getBinding() );
  }

  /**
   * Include the fragment in the object graph.
   * It is assumed that the types included by the fragment have already been included in the ObjectGraph.
   *
   * @param fragment the fragment.
   */
  void registerFragment( @Nonnull final FragmentDescriptor fragment )
  {
    _fragments.put( fragment.getElement().getQualifiedName().toString(), fragment );
    for ( final Binding binding : fragment.getBindings() )
    {
      registerBinding( binding );
    }
  }
}
