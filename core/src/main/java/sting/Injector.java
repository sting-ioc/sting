package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates an interface or abstract class for which a fully-formed, dependency-injected
 * implementation is to be generated from a set of {@linkplain #modules}. The generated class will
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
 * annotated type or a {@link Provides @Provides} annotated method.
 *
 * <a name="instantiation"></a>
 * <h2>Instantiation</h2>
 *
 * <p>If a nested {@link Factory @Factory} type exists in the component, an implementation of that type
 * will generated and an instance will be returned via a static method named {@code factory()}.</p>
 *
 * <p>Example of using a factory:</p>
 *
 * <pre><code>
 * {@literal @}Injector(modules = {BackendModule.class, FrontendModule.class})
 * interface MyInjector {
 *   MyWidget myWidget();
 *
 *   {@literal @}Factory
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
 * <p>If a nested {@link Factory @Factory} does not exist then it is assumed that the component
 * is completed and the generated component will have a factory method {@code create()}.</p>
 *
 * <p>Example of using create:</p>
 *
 * <pre><code>
 * {@literal @}Injector(modules = {BackendModule.class, FrontendModule.class})
 * interface MyInjector {
 *   MyWidget myWidget();
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyInjector injector = StingMyInjector.create();
 *   }
 * }</code></pre>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Injector
{
  /**
   * A list of classes annotated with {@link Module} whose bindings are used to generate the
   * injector implementation. Note that through the use of {@link Module#includes} the full set of
   * modules used to implement the component may include more modules that just those listed here.
   */
  Class<?>[] modules() default {};
}
