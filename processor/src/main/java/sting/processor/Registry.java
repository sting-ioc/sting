package sting.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class Registry {
    /**
     * The set of factories registered.
     */
    private final Map<String, FactoryDescriptor> _factories = new HashMap<>();
    /**
     * The set of injectables registered.
     */
    private final Map<String, InjectableDescriptor> _injectables = new HashMap<>();
    /**
     * The set of fragments registered.
     */
    private final Map<String, FragmentDescriptor> _fragments = new HashMap<>();
    /**
     * The set of injectors registered.
     */
    private final List<InjectorDescriptor> _injectors = new ArrayList<>();
    /**
     * The set of interceptor implementation descriptors registered by class name.
     */
    private final Map<String, InterceptorDescriptor> _interceptors = new HashMap<>();
    /**
     * The set of interceptor proxy descriptors registered by descriptor id.
     */
    private final Map<String, InterceptorProxyDescriptor> _interceptorProxies = new HashMap<>();

    void registerFactory(final FactoryDescriptor factory) {
        _factories.put(factory.getElement().getQualifiedName().toString(), factory);
    }

    Collection<FactoryDescriptor> getFactories() {
        return _factories.values();
    }

    /**
     * Register the Injectable in the local cache.
     *
     * @param injectable the injectable.
     */
    void registerInjectable(final InjectableDescriptor injectable) {
        _injectables.put(injectable.getElement().getQualifiedName().toString(), injectable);
    }

    @Nullable
    InjectableDescriptor findInjectableByClassName(final String name) {
        return _injectables.get(name);
    }

    InjectableDescriptor getInjectableByClassName(final String name) {
        return Objects.requireNonNull(findInjectableByClassName(name));
    }

    Collection<InjectableDescriptor> getInjectables() {
        return _injectables.values();
    }

    void registerInterceptor(final InterceptorDescriptor interceptor) {
        _interceptors.put(interceptor.element().getQualifiedName().toString(), interceptor);
    }

    @Nullable
    InterceptorDescriptor findInterceptorByClassName(final String name) {
        return _interceptors.get(name);
    }

    InterceptorProxyDescriptor findOrCreateInterceptorProxy(final InterceptedServiceDescriptor service) {
        final var descriptor = new InterceptorProxyDescriptor(service);
        return _interceptorProxies.computeIfAbsent(descriptor.getId(), id -> descriptor);
    }

    Collection<InterceptorProxyDescriptor> getInterceptorProxies() {
        return _interceptorProxies.values();
    }

    /**
     * Register the fragment in the local cache.
     *
     * @param fragment the fragment.
     */
    void registerFragment(final FragmentDescriptor fragment) {
        _fragments.put(fragment.getElement().getQualifiedName().toString(), fragment);
    }

    @Nullable
    FragmentDescriptor findFragmentByClassName(final String name) {
        return _fragments.get(name);
    }

    FragmentDescriptor getFragmentByClassName(final String name) {
        return Objects.requireNonNull(findFragmentByClassName(name));
    }

    Collection<FragmentDescriptor> getFragments() {
        return _fragments.values();
    }

    /**
     * Register the injector in the local cache.
     *
     * @param injector the injector.
     */
    void registerInjector(final InjectorDescriptor injector) {
        _injectors.add(injector);
    }

    void deregisterInjector(final InjectorDescriptor injector) {
        _injectors.remove(injector);
    }

    List<InjectorDescriptor> getInjectors() {
        return _injectors;
    }

    void clear() {
        _factories.clear();
        _injectables.clear();
        _fragments.clear();
        _injectors.clear();
        _interceptors.clear();
        _interceptorProxies.clear();
    }
}
