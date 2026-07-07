package sting.processor;

final class Constants {
    static final String INJECTABLE_CLASSNAME = "sting.Injectable";
    static final String FRAGMENT_CLASSNAME = "sting.Fragment";
    static final String INJECTOR_CLASSNAME = "sting.Injector";
    static final String FACTORY_CLASSNAME = "sting.Factory";
    static final String INJECTOR_FRAGMENT_CLASSNAME = "sting.InjectorFragment";
    static final String STING_PROVIDER_CLASSNAME = "sting.StingProvider";
    static final String ACT_AS_STING_CONSUMER_CLASSNAME = "sting.ActAsStingConsumer";
    static final String ACT_AS_STING_PROVIDER_CLASSNAME = "sting.ActAsStingProvider";
    static final String ACT_AS_STING_COMPONENT_CLASSNAME = "sting.ActAsStingComponent";
    static final String INTERCEPTOR_BEFORE_CLASSNAME = "sting.interceptors.Before";
    static final String INTERCEPTOR_AROUND_CLASSNAME = "sting.interceptors.Around";
    static final String INTERCEPTOR_AFTER_CLASSNAME = "sting.interceptors.After";
    static final String INTERCEPTOR_AFTER_EXCEPTION_CLASSNAME = "sting.interceptors.AfterException";
    static final String INTERCEPTOR_SERVICE_TYPE_CLASSNAME = "sting.interceptors.ServiceType";
    static final String INTERCEPTOR_METHOD_NAME_CLASSNAME = "sting.interceptors.MethodName";
    static final String INTERCEPTOR_BINDING_VALUE_CLASSNAME = "sting.interceptors.BindingValue";
    static final String INTERCEPTOR_ARGUMENTS_CLASSNAME = "sting.interceptors.Arguments";
    static final String INTERCEPTOR_PROCEED_CLASSNAME = "sting.interceptors.Proceed";
    static final String INTERCEPTOR_INVOCATION_CLASSNAME = "sting.interceptors.Invocation";
    static final String INTERCEPTOR_RESULT_CLASSNAME = "sting.interceptors.Result";
    static final String INTERCEPTOR_THROWN_CLASSNAME = "sting.interceptors.Thrown";
    static final String INTERCEPTOR_BINDING_SIMPLE_NAME = "InterceptorBinding";
    static final String ACT_AS_STING_CONSUMER_SIMPLE_NAME = "ActAsStingConsumer";
    static final String ACT_AS_STING_PROVIDER_SIMPLE_NAME = "ActAsStingProvider";
    static final String ACT_AS_STING_COMPONENT_SIMPLE_NAME = "ActAsStingComponent";
    static final String INPUT_CLASSNAME = "sting.Injector.Input";
    static final String NAMED_CLASSNAME = "sting.Named";
    static final String JSR_330_NAMED_CLASSNAME = "javax.inject.Named";
    static final String JSR_330_INJECT_CLASSNAME = "javax.inject.Inject";
    static final String JSR_330_SCOPE_CLASSNAME = "javax.inject.Scope";
    static final String EAGER_CLASSNAME = "sting.Eager";
    static final String TYPED_CLASSNAME = "sting.Typed";
    static final String CDI_TYPED_CLASSNAME = "javax.enterprise.inject.Typed";
    static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
    static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";
    static final String WARNING_JSR_330_NAMED = "Sting:Jsr330NamedPresent";
    static final String WARNING_JSR_330_INJECT = "Sting:Jsr330InjectPresent";
    static final String WARNING_JSR_330_SCOPED = "Sting:Jsr330ScopedPresent";
    static final String WARNING_CDI_TYPED = "Sting:CdiTypedPresent";
    static final String WARNING_REDUNDANT_TYPED_ANNOTATION = "Sting:RedundantTypedAnnotation";
    static final String WARNING_AUTO_DISCOVERABLE_INCLUDED = "Sting:AutoDiscoverableIncluded";
    static final String WARNING_REDUNDANT_DIRECT_INJECTABLE_INCLUDE = "Sting:RedundantExplicitInjectableInclude";
    static final String WARNING_FRAGMENT_INCLUDE_CYCLE = "Sting:FragmentIncludeCycle";

    private Constants() {}
}
