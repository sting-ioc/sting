package sting.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.json.stream.JsonGenerator;
import org.jspecify.annotations.Nullable;

final class ComponentGraph {
    /**
     * The processor building the graph.
     */
    private final StingProcessor _processor;
    /**
     * The injector that defines the graph.
     */
    private final InjectorDescriptor _injector;

    private final Registry _registry;
    /**
     * The list of types included in the graph.
     * This is used to skip registers for types that are already present.
     * This can occur when we have diamond dependency chains.
     */
    private final Set<String> _includedTypes = new HashSet<>();
    /**
     * A mapping of the root include element to the bindings.
     * It is used to verify that a particular include root is used within an injector.
     */
    private final Map<IncludeDescriptor, Set<Binding>> _includeRootToBindingMap = new HashMap<>();
    /**
     * The types that are published in the component graph.
     */
    private final Map<ServiceKey, List<Binding>> _publishedTypes = new LinkedHashMap<>();
    /**
     * The index of ids to Node.
     */
    private final Map<String, Node> _nodesById = new HashMap<>();
    /**
     * The node that represents the Injector.
     */
    private final Node _rootNode;
    /**
     * true when the graph has been completely built.
     */
    private boolean _complete;
    /**
     * The list of nodes included in graph in stable order based on depth in graph and node id.
     * The ordering guarantees that non-supplier dependencies occur earlier than the components
     * that consume the dependencies.
     */
    @Nullable
    private List<Node> _orderedNodes;
    /**
     * The list of fragment nodes included in graph in stable order based on name.
     */
    @Nullable
    private List<FragmentNode> _fragmentNodes;

    ComponentGraph(final StingProcessor processor, final InjectorDescriptor injector, final Registry registry) {
        _processor = Objects.requireNonNull(processor);
        _injector = Objects.requireNonNull(injector);
        _registry = Objects.requireNonNull(registry);
        _rootNode = new Node(this);
    }

    InjectorDescriptor getInjector() {
        return _injector;
    }

    Node getRootNode() {
        return _rootNode;
    }

    Collection<Node> getRawNodeCollection() {
        return _nodesById.values();
    }

    Node findOrCreateNode(final Binding binding) {
        assert !_complete;
        final String id = binding.getId();
        final Node node = _nodesById.get(id);
        if (null == node) {
            final Node newNode = createNode(binding);
            _nodesById.put(id, newNode);
            return newNode;
        } else {
            return node;
        }
    }

    private Node createNode(final Binding binding) {
        final String id = binding.getId();
        assert !_nodesById.containsKey(id);
        final var node = new Node(this, binding);
        _nodesById.put(id, node);
        return node;
    }

    List<Node> getNodes() {
        return Objects.requireNonNull(_orderedNodes);
    }

    List<FragmentNode> getFragments() {
        return Objects.requireNonNull(_fragmentNodes);
    }

    void complete() {
        assert !_complete;
        _complete = true;
        final var index = new AtomicInteger();
        final var fragmentMap = new HashMap<FragmentDescriptor, FragmentNode>();
        _fragmentNodes = _nodesById.values().stream()
                .filter(Node::isFromProvides)
                .map(n -> (FragmentDescriptor) n.getBinding().getOwner())
                .sorted(Comparator.comparing(FragmentDescriptor::getQualifiedTypeName))
                .map(f -> new FragmentNode(f, "fragment" + index.incrementAndGet()))
                .peek(f -> fragmentMap.put(f.fragment(), f))
                .collect(Collectors.toList());
        index.set(0);
        _orderedNodes = sortNodes(_nodesById.values());
        for (final Node node : _orderedNodes) {
            node.setName("node" + index.incrementAndGet());
            if (node.isFromProvides()) {
                //noinspection SuspiciousMethodCalls
                node.setFragment(
                        Objects.requireNonNull(fragmentMap.get(node.getBinding().getOwner())));
            }
        }
    }

    private List<Node> sortNodes(final Collection<Node> nodes) {
        final var results = new ArrayList<Node>(nodes.size());
        final var workList = new ArrayList<Node>(nodes);
        final var done = new HashSet<Node>();
        while (!workList.isEmpty()) {
            final Node node = workList.remove(workList.size() - 1);
            processNode(node, results, done);
        }
        return results;
    }

    private void processNode(final Node node, final List<Node> results, final Set<Node> done) {
        if (!done.contains(node)) {
            done.add(node);
            for (final Edge edge : node.getDependsOn()) {
                for (final Node other : edge.getSatisfiedBy()) {
                    processNode(other, results, done);
                }
            }
            results.add(node);
        }
    }

    /**
     * Register the binding in the component graph.
     *
     * @param binding the binding.
     */
    private void registerBinding(final Binding binding) {
        for (final var service : binding.getPublishedServices()) {
            _publishedTypes
                    .computeIfAbsent(new ServiceKey(service.getCoordinate()), c -> new ArrayList<>())
                    .add(binding);
        }
    }

    private void registerEagerBinding(final Binding binding) {
        _processor.processInterceptorBindings(binding);
        var proxiedService = false;
        for (final var service : binding.getPublishedServices()) {
            final var interceptedService = binding.findInterceptedService(service.getCoordinate());
            if (null != interceptedService) {
                findOrCreateNode(binding);
                final var proxyNode = findOrCreateNode(_registry.findOrCreateInterceptorProxy(interceptedService));
                attachProxyDependencies(proxyNode);
                proxiedService = true;
            }
        }
        if (!proxiedService) {
            findOrCreateNode(binding);
        }
    }

    Map<IncludeDescriptor, Set<Binding>> getIncludeRootToBindingMap() {
        return _includeRootToBindingMap;
    }

    List<Binding> findAllBindingsByCoordinate(final Coordinate coordinate) {
        return _publishedTypes.getOrDefault(new ServiceKey(coordinate), Collections.emptyList());
    }

    Node findOrCreateProviderNode(final Binding binding, final Coordinate coordinate) {
        _processor.processInterceptorBindings(binding);
        final var interceptedService = binding.findInterceptedService(coordinate);
        return null == interceptedService
                ? findOrCreateNode(binding)
                : findOrCreateNode(_registry.findOrCreateInterceptorProxy(interceptedService));
    }

    Node findOrCreateNode(final InterceptorProxyDescriptor proxy) {
        assert !_complete;
        final var id = proxy.getId();
        final var node = _nodesById.get(id);
        if (null == node) {
            final var newNode = new Node(this, proxy);
            _nodesById.put(id, newNode);
            return newNode;
        } else {
            return node;
        }
    }

    void attachProxyDependencies(final Node proxyNode) {
        assert proxyNode.isProxy();
        if (proxyNode.getDependsOn().isEmpty()) {
            final var proxy = proxyNode.getProxy();
            final var service = proxy.getService();
            final var targetNode = findOrCreateNode(service.binding());
            proxyNode.addResolvedDependency(
                    new ServiceRequest(
                            ServiceRequest.Kind.INSTANCE,
                            service.service(),
                            service.binding().getElement()),
                    targetNode);
            for (final var binding : proxy.getGenericInterceptorBindings()) {
                final var interceptorService = binding.getPublishedServices().get(0);
                final var interceptorNode = findOrCreateNode(binding);
                proxyNode.addResolvedDependency(
                        new ServiceRequest(ServiceRequest.Kind.INSTANCE, interceptorService, binding.getElement()),
                        interceptorNode);
            }
        }
    }

    /**
     * Include the input in the component graph.
     *
     * @param input the input.
     */
    void registerInput(final InputDescriptor input) {
        final Binding binding = input.binding();
        registerBinding(binding);
        findOrCreateNode(binding);
    }

    /**
     * Include the injectable in the component graph.
     *
     * @param includeRoot the root include that included the injectable.
     * @param injectable  the injectable.
     */
    void registerInjectable(final IncludeDescriptor includeRoot, final InjectableDescriptor injectable) {
        _includeRootToBindingMap
                .computeIfAbsent(includeRoot, r -> new HashSet<>())
                .add(injectable.getBinding());
        doRegisterInjectable(injectable);
    }

    void registerInjectable(final InjectableDescriptor injectable) {
        doRegisterInjectable(injectable);
    }

    private void doRegisterInjectable(final InjectableDescriptor injectable) {
        final String typeName = injectable.getElement().getQualifiedName().toString();
        if (_includedTypes.add(typeName)) {
            final Binding binding = injectable.getBinding();
            registerBinding(binding);
            if (binding.isEager()) {
                registerEagerBinding(binding);
            }
        }
    }

    /**
     * Include the fragment in the component graph.
     * It is assumed that the types included by the fragment have already been included in the ObjectGraph.
     *
     * @param includeRoot the root include that ultimately included the fragment.
     * @param fragment    the fragment.
     */
    void registerFragment(final IncludeDescriptor includeRoot, final FragmentDescriptor fragment) {
        _includeRootToBindingMap
                .computeIfAbsent(includeRoot, r -> new HashSet<>())
                .addAll(fragment.getBindings());
        doRegisterFragment(fragment);
    }

    void registerFragment(final FragmentDescriptor fragment) {
        doRegisterFragment(fragment);
    }

    private void doRegisterFragment(final FragmentDescriptor fragment) {
        final String typeName = fragment.getElement().getQualifiedName().toString();
        if (_includedTypes.add(typeName)) {
            for (final Binding binding : fragment.getBindings()) {
                registerBinding(binding);
                if (binding.isEager()) {
                    registerEagerBinding(binding);
                }
            }
        }
    }

    void write(final JsonGenerator g) {
        g.writeStartObject();
        g.write("schema", "graph/1");

        g.writeStartArray("nodes");

        for (final Node node : getNodes()) {
            node.write(g);
        }
        g.writeEnd();

        g.writeEnd();
    }
}
