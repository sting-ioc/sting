package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Annotates an interface or abstract class for which a fully-formed, dependency-injected
 * implementation is to be generated from the {@link #includes() included} object graph fragments. The generated class will
 * have the name of the type annotated with {@code @Injector} prepended with {@code Sting_}. For
 * example, {@code @Injector interface MyInjector {...}} will produce an implementation named
 * {@code Sting_MyInjector}.
 *
 * <a name="component-methods"></a>
 * <h2>Component methods</h2>
 *
 * <p>Every type annotated with {@code @Injector} must contain at least one abstract component
 * method. Every abstract method must return a type provided by the object graph and must not
 * have any parameters. The dependency should be provisioned by a {@link Injectable @Injectable}
 * annotated type or a factory method in a {@link Fragment @Fragment} annotated type.
 *
 * <a name="instantiation"></a>
 * <h2>Instantiation</h2>
 *
 * <p>If a nested interface named Factory exists in the component, an implementation of the Factory interface
 * will be generated and an instance will be returned via a static method named {@code factory()}.</p>
 *
 * <p>Example of using a factory:</p>
 *
 * <pre><code>
 * {@literal @}Injector(includes = {BackendFragment.class, FrontendFragment.class})
 * interface MyInjector {
 *   MyWidget myWidget();
 *
 *   interface Factory {
 *     MyInjector create(MyService myService);
 *   }
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyService myService = ...;
 *     MyInjector injector = Sting_MyInjector.factory().create(myService);
 *   }
 * }</code></pre>
 *
 *
 * <p>If a nested interface named Factory does not exist then it is assumed that the component
 * is self-contained and the generated component will have a factory method {@code create()}.</p>
 *
 * <p>Example of using create:</p>
 *
 * <pre><code>
 * {@literal @}Injector(includes = {BackendFragment.class, FrontendFragment.class})
 * interface MyInjector {
 *   MyWidget myWidget();
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyInjector injector = StingMyInjector.create();
 *   }
 * }</code></pre>
 *
 * <h3>Circular Dependencies</h3>
 *
 * <p>Circular dependencies are disallowed by the injector and are rejected during the compilation phase.
 * The developer can break the circular dependency by injecting {@link Supplier Supplier&lt;OtherType>}
 * instead of {@code OtherType} and then calling {@link Supplier#get() get()} on the supplier when access
 * to the dependency is needed.</p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
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
  Service[] inputs() default {};

  /**
   * A specification of a service that is supplied to an injector during construction.
   * The service is added to the the component graph and is made available for other components to consume
   */
  @Retention( RetentionPolicy.RUNTIME )
  @Documented
  @Target( {} )
  @interface Service
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
  }
}
