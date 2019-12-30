package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Identifies an injectable type.
 * Types annotated with this are expected to be created by an injector.
 * The type must be concrete and must have a single package-access constructor. The constructor can accept zero or
 * more dependencies as arguments.
 *
 * <b>Optional Dependencies</b>
 *
 * <p>A non-primitive parameter can be marked as optional by annotating it with the
 * {@linkplain Nullable @Nullable} annotation. If the injector can not find a value to satisfy the dependency
 * then the injector may pass null for the parameter.</p>
 *
 * <b>Qualified Dependencies</b>
 *
 * A parameter may can also be annotated with the {@link Qualifier @Qualifier} annotation and the value must
 * have a matching qualifier to satisfy the dependency.
 *
 * <h3>Circular Dependencies</h3>
 *
 * <p>Circular dependencies are disallowed by the injector and are rejected during the compilation phase.
 * The developer can break the circular dependency by injecting {@link Supplier Supplier&lt;OtherType>}
 * instead of {@code OtherType} and then calling {@link Supplier#get() get()} on the supplier when access
 * to the dependency is needed.</p>
 *
 * @see Qualifier @Qualifier
 * @see Supplier
 */
@Target( ElementType.TYPE )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface Injectable
{
  /**
   * A flag indicating whether the instance should be instantiated when the Injector is created.
   *
   * @return a flag indicating whether the instance should be instantiated when the Injector is created.
   */
  boolean eager() default false;

  /**
   * An opaque string that qualifies this type.
   * This can be any arbitrary string and is used to restrict the dependencies that this type can satisfy.
   *
   * @return an opaque qualifier string.
   */
  @Nonnull
  String qualifier() default "";

  /**
   * The types of dependency that this binding can satisfy.
   * By default the binding will match the type that is annotated.
   * If specified, the binding will only match the types specified and
   * every type must be assignable from the type that is annotated.
   *
   * @return the types of dependency that this binding can satisfy.
   */
  Class<?>[] types() default void.class;
}
