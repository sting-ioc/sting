package sting.processor;

import javax.annotation.Nonnull;

final class Constants {
    @Nonnull
    static final String INJECTABLE_CLASSNAME = "sting.Injectable";

    @Nonnull
    static final String FRAGMENT_CLASSNAME = "sting.Fragment";

    @Nonnull
    static final String INJECTOR_CLASSNAME = "sting.Injector";

    @Nonnull
    static final String FACTORY_CLASSNAME = "sting.Factory";

    @Nonnull
    static final String INJECTOR_FRAGMENT_CLASSNAME = "sting.InjectorFragment";

    @Nonnull
    static final String STING_PROVIDER_CLASSNAME = "sting.StingProvider";

    @Nonnull
    static final String ACT_AS_STING_CONSUMER_CLASSNAME = "sting.ActAsStingConsumer";

    @Nonnull
    static final String ACT_AS_STING_PROVIDER_CLASSNAME = "sting.ActAsStingProvider";

    @Nonnull
    static final String ACT_AS_STING_COMPONENT_CLASSNAME = "sting.ActAsStingComponent";

    @Nonnull
    static final String INTERCEPTOR_BEFORE_CLASSNAME = "sting.interceptors.Before";

    @Nonnull
    static final String INTERCEPTOR_AROUND_CLASSNAME = "sting.interceptors.Around";

    @Nonnull
    static final String INTERCEPTOR_AFTER_CLASSNAME = "sting.interceptors.After";

    @Nonnull
    static final String INTERCEPTOR_AFTER_EXCEPTION_CLASSNAME = "sting.interceptors.AfterException";

    @Nonnull
    static final String INTERCEPTOR_SERVICE_TYPE_CLASSNAME = "sting.interceptors.ServiceType";

    @Nonnull
    static final String INTERCEPTOR_METHOD_NAME_CLASSNAME = "sting.interceptors.MethodName";

    @Nonnull
    static final String INTERCEPTOR_BINDING_VALUE_CLASSNAME = "sting.interceptors.BindingValue";

    @Nonnull
    static final String INTERCEPTOR_ARGUMENTS_CLASSNAME = "sting.interceptors.Arguments";

    @Nonnull
    static final String INTERCEPTOR_PROCEED_CLASSNAME = "sting.interceptors.Proceed";

    @Nonnull
    static final String INTERCEPTOR_INVOCATION_CLASSNAME = "sting.interceptors.Invocation";

    @Nonnull
    static final String INTERCEPTOR_RESULT_CLASSNAME = "sting.interceptors.Result";

    @Nonnull
    static final String INTERCEPTOR_THROWN_CLASSNAME = "sting.interceptors.Thrown";

    @Nonnull
    static final String INTERCEPTOR_BINDING_SIMPLE_NAME = "InterceptorBinding";

    @Nonnull
    static final String ACT_AS_STING_CONSUMER_SIMPLE_NAME = "ActAsStingConsumer";

    @Nonnull
    static final String ACT_AS_STING_PROVIDER_SIMPLE_NAME = "ActAsStingProvider";

    @Nonnull
    static final String ACT_AS_STING_COMPONENT_SIMPLE_NAME = "ActAsStingComponent";

    @Nonnull
    static final String INPUT_CLASSNAME = "sting.Injector.Input";

    @Nonnull
    static final String NAMED_CLASSNAME = "sting.Named";

    @Nonnull
    static final String JSR_330_NAMED_CLASSNAME = "javax.inject.Named";

    @Nonnull
    static final String JSR_330_INJECT_CLASSNAME = "javax.inject.Inject";

    @Nonnull
    static final String JSR_330_SCOPE_CLASSNAME = "javax.inject.Scope";

    @Nonnull
    static final String EAGER_CLASSNAME = "sting.Eager";

    @Nonnull
    static final String TYPED_CLASSNAME = "sting.Typed";

    @Nonnull
    static final String CDI_TYPED_CLASSNAME = "javax.enterprise.inject.Typed";

    @Nonnull
    static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";

    @Nonnull
    static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

    @Nonnull
    static final String WARNING_JSR_330_NAMED = "Sting:Jsr330NamedPresent";

    @Nonnull
    static final String WARNING_JSR_330_INJECT = "Sting:Jsr330InjectPresent";

    @Nonnull
    static final String WARNING_JSR_330_SCOPED = "Sting:Jsr330ScopedPresent";

    @Nonnull
    static final String WARNING_CDI_TYPED = "Sting:CdiTypedPresent";

    @Nonnull
    static final String WARNING_AUTO_DISCOVERABLE_INCLUDED = "Sting:AutoDiscoverableIncluded";

    @Nonnull
    static final String WARNING_REDUNDANT_DIRECT_INJECTABLE_INCLUDE = "Sting:RedundantExplicitInjectableInclude";

    @Nonnull
    static final String WARNING_FRAGMENT_INCLUDE_CYCLE = "Sting:FragmentIncludeCycle";

    private Constants() {}
}
