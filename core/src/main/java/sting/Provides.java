/*
 * Copyright (C) 2007 The Dagger Authors.
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
import javax.annotation.Nonnull;

/**
 * Annotates methods of a {@linkplain Module module} to create a provider method binding. The
 * method's return type is bound to its returned value. The {@linkplain Injector injector}
 * implementation will pass dependencies to the method as parameters.
 *
 * <h3>Nullability</h3>
 *
 * <p>Sting forbids injecting {@code null} by default. Component implementations that invoke
 * {@code @Provides} methods that return {@code null} will throw a {@link NullPointerException}
 * immediately thereafter. {@code @Provides} methods may opt into allowing {@code null} by
 * annotating the method with any {@code @Nullable} annotation like
 * {@code javax.annotation.Nullable}.
 *
 * <p>If a {@code @Provides} method is marked {@code @Nullable}, Sting will <em>only</em>
 * allow injection into sites that are marked {@code @Nullable} as well. A component that
 * attempts to pair a {@code @Nullable} provision with a non-{@code @Nullable} injection site
 * will fail to compile.
 */
@Documented
@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Provides
{
  /**
   * An opaque string that qualifies this binding.
   * This can be any arbitrary string and is used to restrict the dependencies that this binding can satisfy.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier() default "";

  /**
   * The types of dependency that this binding can satisfy.
   * By default the binding will match the return type of the annotated method.
   * If specified, the binding will only match the types specified and
   * every type must be assignable from the return type of the annotated method
   *
   * @return the types of dependency that this binding can satisfy.
   */
  Class<?>[] types() default void.class;
}
