package sting.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;

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
  /**
   * The nodes contained in the object graph.
   */
  @Nonnull
  private final Map<Binding, Node> _nodes = new HashMap<>();
  /**
   * The node that represents the Injector.
   */
  @Nonnull
  private final Node _rootNode;

  ObjectGraph( @Nonnull final InjectorDescriptor injector )
  {
    _injector = Objects.requireNonNull( injector );
    _rootNode = new Node( injector.getTopLevelDependencies().toArray( new DependencyDescriptor[ 0 ] ) );
  }

  @Nonnull
  InjectorDescriptor getInjector()
  {
    return _injector;
  }

  @Nonnull
  Node getRootNode()
  {
    return _rootNode;
  }

  @Nonnull
  Node findOrCreateNode( @Nonnull final Binding binding )
  {
    return _nodes.computeIfAbsent( binding, Node::new );
  }

  /**
   * Register the binding in the object graph.
   *
   * @param binding the binding.
   */
  private void registerBinding( @Nonnull final Binding binding )
  {
    _bindings.add( binding );
    binding.getCoordinates()
      .forEach( coordinate -> _publishedTypes.computeIfAbsent( coordinate, c -> new ArrayList<>() ).add( binding ) );
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

  void write( final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "objectGraph/1" );
    g.writeEnd();
  }
}
