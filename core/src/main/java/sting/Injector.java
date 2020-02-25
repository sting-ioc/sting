package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Annotates an interface for which a dependency-injected implementation is to be generated.
 * The implementation builds a component graph from the {@link #includes() included} types and
 * exposes the services declared on the injector interface.
 *
 * <h2>Component Graph</h2>
 *
 * <p>The component graph is created in multiple phases.</p>
 *
 * <p>The first phase involves collecting all the types that are declared via the {@link #includes()}
 * property and adding a binding (a.k.a. a potential component) for every {@link Injectable} annotated
 * type and a binding for every method in {@link Fragment} annotated types. </p>
 *
 * <p>The second phase involves building a set of actual components created by the injector. Any potential
 * binding that is annotated with the {@link Eager} annotation is added to the set of components. The
 * {@link #inputs() inputs} are also modelled as eager components. The compiler then examines the services
 * declared by the <a href="#service-methods">service methods</a> and attempts to resolve the services into
 * components. When a component is added to the list of component, the service dependencies are added to the
 * sert of services to resolve. When there is no services left to resolve, the injector is considered
 * complete and the compiler terminates this phase.</p>
 *
 * <p>The next phase will identify the binding for each component will mark the component as eager and if the
 * binding is annotated with the {@link Eager} annotation. All dependencies  of the component that are not
 * {@link Supplier} dependencies are marked as eager. This process is recursively applied to dependencies of
 * dependencies. At the end of this phase the components are all categorized into those that are eager and
 * created when the injector is instantiated and components that are lazy and are created on demand.</p>
 *
 * <p>The final phase performs validation and correctness checking. It is during this phase that circular
 * dependencies are detected and rejected and that injection requests for injection of singular values with
 * multiple bindings that satisfy are detected.</p>
 *
 * <h2>Resolving Services</h2>
 *
 * <p>A service is resolved into a component by looking at the set of bindings present in the injector.
 * If a binding exists that publishes the same type with the same qualifier then the binding is considered
 * a match.</p>
 *
 * <p>If the binding is marked as optional (i.e. the component is created by a method annotated with
 * {@link javax.annotation.Nullable} in a type annotated with the {@link Fragment} annotation) and the service
 * is not optional then the compiler generates an error as it is not able to determine statically that the
 * service will be available.</p>
 *
 * <p>If no matching binding is found then the compiler will attempt to look for a class annotated with
 * {@link Injectable} that has the same name as the type of the service. If found and the type matches the service
 * then the class will be added to the set of components created. If not found and the service is not optional then
 * the compiler will generate an error.</p>
 *
 * <h2>Generated Classname</h2>
 *
 * <p>The generated class will have the name of the type annotated with the {@code @Injector} annotation
 * prepended with {@code Sting_}. For example, the class {@code mybiz.MyInjector} will produce
 * an implementation named {@code mybiz.Sting_MyInjector}. Nested classes are also supported but their names
 * have the {@code $} sign replaced with a {@code _}. i.e. The nested class named {@code mybiz.MyOuterClass.MyInjector}
 * will generate an implementation named {@code mybiz.MyOuterClass_Sting_MyInjector}</p>
 *
 * <a name="service-methods">Service methods</a>
 * <h2>Service methods</h2>
 *
 * <p>Instance methods defined on the injector allow access to services contained within the injector and
 * also define the root services that are used to build the component graph. The instance methods must be
 * abstract, have zero parameters, throw no exceptions and return services. The methods can be annotated with
 * {@link Named} to qualify the service and {@link javax.annotation.Nullable} to mark the service as optional.</p>
 *
 * <h2>Instantiation</h2>
 *
 * <p>A injector is created by invoking the constructor of the generated class. The generated class is package
 * access so unless you are only using the injector from within the package it was created, you need to expose
 * a method to create the injector. The usual pattern is to define a static method named {@code create} on the
 * injector interface that creates an instance of the injector.</p>
 *
 * <p>Example:</p>
 *
 * <pre><code>
 * {@literal @}Injector(includes = {BackendFragment.class, FrontendFragment.class})
 * public interface MyInjector {
 *
 *   public static MyInjector create() {
 *     return new Sting_MyInjector();
 *   }
 *
 *   MyWidget myWidget();
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyInjector injector = MyInjector.create();
 *     ... injector.myWidget() ...
 *   }
 * }</code></pre>
 *
 *
 * <p>The {@link Injector} annotation use the {@link #inputs()} parameter that declares services that are
 * passed into the injector. These services are made available to components within the component graph and
 * maybe be qualified and/or marked as optional. Each input service is supplied to the injector as a constructor
 * parameter in the generated class.</p>
 *
 * <p>Example of using input services:</p>
 *
 * <pre><code>
 * {@literal @}Injector( includes = {BackendFragment.class, FrontendFragment.class},
 *            inputs = { {@literal @}Injector.Input( type = MyService.class ),
 *                       {@literal @}Injector.Input( qualifier = "hostname", type = String.class ) } )
 * interface MyInjector {
 *   MyWidget myWidget();
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyService service = ...;
 *     MyInjector injector = new Sting_MyInjector(service, "mybiz.com");
 *   }
 * }</code></pre>
 *
 * <h3>Circular Dependencies</h3>
 *
 * <p>Circular dependencies within the injector are detected and rejected during the compilation phase.
 * Circular dependencies can be broken by passing a {@link Supplier} dependency. i.e The developer injects
 * the type {@link Supplier Supplier&lt;OtherType>} instead of {@code OtherType} and then calls {@link Supplier#get()}
 * on the supplier when access to the service is needed.</p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
@StingProvider( "[FlatEnclosingName]Sting_[SimpleName]_Provider" )
public @interface Injector
{
  /**
   * A list of types that contribute to the object graph.
   * These types can be {@code @Fragment}-annotated interfaces or {@link Injectable @Injectable}-annotated classes.
   * The de-duplicated contributions of the {@code @Fragment}-annotated interfaces in the
   * {@code includes}, and of their inclusions recursively, are all contributed
   * to the object graph.
   *
   * <p>If the annotation processor detects a dependency that is required but not explicitly included in the
   * includes list then it will attempt to automatically add the type to the graph if it is annotated with
   * {@link Injectable @Injectable}. The current implementation include types
   * if they were compiled in the same invocation of the java compiler. In the future the annotation processor
   * will load the descriptors from the filesystem.</p>
   *
   * @return a list of types that contribute to the injectors object graph.
   */
  Class<?>[] includes() default {};

  /**
   * A list of services that must be passed into the injector.
   * The annotation processor will generate a constructor with one parameter for every input. Each input
   * value MUST specify the type parameter otherwise the annotation processor is unable to determine the
   * type of the binding.
   *
   * @return a list of services that must be passed into the injector.
   */
  Input[] inputs() default {};

  /**
   * A flag controlling whether the injector implementation can be added to other injectors.
   * If set to true then the injector can be included in another injector. The {@link #inputs()}
   * are services that need to be provided while the service methods will define services that this
   * injector provides.
   *
   * @return true to make the injector able to be included in another injector, false otehrwise.
   */
  boolean injectable() default false;

  /**
   * A specification of a service that is supplied to an injector during construction.
   * The service is added to the the component graph and is made available for other components to consume
   */
  @Retention( RetentionPolicy.RUNTIME )
  @Documented
  @Target( {} )
  @interface Input
  {
    /**
     * An opaque string that qualifies the service.
     * The string is user-supplied and used to distinguish two different services with the same {@link #type()}
     * but different semantics.
     *
     * @return an opaque qualifier string.
     */
    @Nonnull
    String qualifier() default "";

    /**
     * The java type of the service.
     *
     * <p>Sting does not support classes defined with type parameters.</p>
     *
     * @return the java type of the service.
     */
    Class<?> type();

    /**
     * A flag indicating whether the input is optional and may be null or required.
     *
     * @return a flag indicating whether the input is optional and may be null or required.
     */
    boolean optional() default false;
  }
}
