package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;
import javax.annotation.Nullable;

/**
 * Identifies injectable type.
 * Types annotated with this are candidates for injection and are only expected to be exposed via injection.
 * The type must be concrete and must have a single package-access constructor. The constructor can accept zero or
 * more dependencies as arguments. A parameter can be annotated with {@linkplain Nullable @Nullable}
 * to indicate that the dependency is optional. A parameter can also be annotated with zero or one
 * {@linkplain Qualifier qualifier} annotations. The {@linkplain Qualifier qualifier} combined with the type
 * identifies the implementation to inject.
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
}
