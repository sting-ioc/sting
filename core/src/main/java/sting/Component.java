/*
 * Copyright (C) 2014 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.inject.Inject;

/**
 * Annotates an interface or abstract class for which a fully-formed, dependency-injected
 * implementation is to be generated from a set of {@linkplain #modules}. The generated class will
 * have the name of the type annotated with {@code @Component} prepended with {@code Sting}. For
 * example, {@code @Component interface MyComponent {...}} will produce an implementation named
 * {@code StingMyComponent}.
 *
 * <a name="component-methods"></a>
 * <h2>Component methods</h2>
 *
 * <p>Every type annotated with {@code @Component} must contain at least one abstract component
 * method. Component methods may have any name, but must be a provision method.
 *
 * <a name="provision-methods"></a>
 * <h3>Provision methods</h3>
 *
 * <p>Provision methods have no parameters and return an {@link Inject injected} or {@link Provides
 * provided} type. The following are all valid provision method declarations:
 *
 * <pre><code>
 *   SomeType getSomeType();
 *   {@literal Set<SomeType>} getSomeTypes();
 * </code></pre>
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
 * {@literal @}Component(modules = {BackendModule.class, FrontendModule.class})
 * interface MyComponent {
 *   MyWidget myWidget();
 *
 *   {@literal @}Factory
 *   interface Factory {
 *     MyComponent create(MyService myService);
 *   }
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyService myService = ...;
 *     MyComponent component = StingMyComponent.factory().create(myService);
 *   }
 * }</code></pre>
 *
 * <p>If a nested {@link Factory @Factory} does not exist then it is assumed that the component
 * is completed and the generated component will have a factory method {@code create()}.</p>
 *
 * <p>Example of using create:</p>
 *
 * <pre><code>
 * {@literal @}Component(modules = {BackendModule.class, FrontendModule.class})
 * interface MyComponent {
 *   MyWidget myWidget();
 * }
 *
 * public class Main {
 *   public static void main(String[] args) {
 *     MyComponent component = StingMyComponent.create();
 *   }
 * }</code></pre>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface Component
{
  /**
   * A list of classes annotated with {@link Module} whose bindings are used to generate the
   * component implementation. Note that through the use of {@link Module#includes} the full set of
   * modules used to implement the component may include more modules that just those listed here.
   */
  Class<?>[] modules() default {};

  /**
   * An annotation applied to an interface responsible for creating a component.
   * The interface must have a single abstract method that returns the component. The interface
   * may take parameters and these are beans that are added to the object graph.
   *
   * For example, this could be a valid {@code Component} with a {@code Factory}:
   *
   * <pre><code>
   * {@literal @}Component(modules = {BackendModule.class, FrontendModule.class})
   * interface MyComponent {
   *   MyWidget myWidget();
   *
   *   {@literal @}Component.Factory
   *   interface Factory {
   *     MyComponent create(MyService myService);
   *   }
   * }</code></pre>
   *
   * <p>If a {@code @Component.Factory} is defined, the generated component type will have a {@code static}
   * method named {@code factory()} that returns an instance of that factory.
   */
  @Documented
  @Retention( RetentionPolicy.RUNTIME )
  @Target( ElementType.TYPE )
  @interface Factory
  {
  }
}
