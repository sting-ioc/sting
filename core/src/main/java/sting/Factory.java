package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface for which a dependency-injected factory implementation is to be generated.
 * The generated implementation is itself a Sting-managed component and publishes the factory interface
 * via the {@link Typed} annotation.
 *
 * <h2>Generated Classname</h2>
 *
 * <p>The generated factory implementation uses the standard Sting naming convention and has the name of the
 * annotated type prepended with {@code Sting_}. For example, the top-level type
 * {@code mybiz.MyWidgetFactory} will produce the implementation {@code mybiz.Sting_MyWidgetFactory}.
 * Nested types are also supported but their names have the {@code $} sign replaced with a {@code _}.
 * i.e. The nested class named {@code mybiz.MyOuterClass.MyWidgetFactory} will generate an implementation
 * named {@code mybiz.MyOuterClass_Sting_MyWidgetFactory}</p>
 *
 * <h2>Factory Methods</h2>
 *
 * <p>Abstract instance methods defined on the factory interface are treated as candidate factory methods.
 * Each candidate method identifies a component type to create and the subset of constructor arguments that
 * are supplied by the caller at runtime. The factory interface may also declare default methods and these are
 * inherited by the generated implementation.</p>
 *
 * <p>Candidate factory methods must:</p>
 *
 * <ul>
 *   <li>return a concrete class type,</li>
 *   <li>not declare type parameters,</li>
 *   <li>not declare thrown exceptions, and</li>
 *   <li>have parameters whose names and types exactly match constructor parameters on the created type.</li>
 * </ul>
 *
 * <p>The created type must have exactly one constructor accessible from the package containing the factory
 * interface. Constructor parameters omitted from the factory method are treated as Sting-managed dependencies
 * and are injected into the generated factory implementation. This makes it possible to combine services managed
 * by Sting with runtime values supplied by application code.</p>
 *
 * <h2>Injector Integration</h2>
 *
 * <p>The {@link Factory} annotation is itself annotated with {@link StingProvider}, allowing the factory
 * interface to be included in an {@link Injector#includes()} or {@link Fragment#includes()} list.
 * The generated factory implementation is annotated as an {@link Injectable} and {@link Typed} component
 * so other Sting-managed components can depend upon the factory interface.</p>
 *
 * <p>A single factory interface may define multiple factory methods that create different component types.</p>
 *
 * <p>Example:</p>
 *
 * <pre><code>
 * class MyWidget {
 *   MyWidget( {@literal @}Nonnull BackendService backendService, int size ) {
 *   }
 * }
 *
 * {@literal @}Factory
 * interface MyWidgetFactory {
 *   {@literal @}Nonnull
 *   MyWidget create( int size );
 * }
 *
 * {@literal @}Injector( includes = MyWidgetFactory.class )
 * interface MyInjector {
 *   MyWidgetFactory widgetFactory();
 * }
 * </code></pre>
 *
 * <p>In the example above, Sting generates a factory implementation that injects the {@code BackendService}
 * dependency and accepts the {@code size} parameter from the caller when {@code create(int)} is invoked.</p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@StingProvider( "[FlatEnclosingName]Sting_[SimpleName]" )
public @interface Factory
{
}
