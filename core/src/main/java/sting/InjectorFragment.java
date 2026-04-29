package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate an interface to declare that it will be used in an injector-oriented integration surface.
 * This annotation is a top-level Sting processor entrypoint used for validation only.
 *
 * <p>Sting tolerates {@link Named} on methods declared by a type annotated with
 * {@link InjectorFragment}, but does not treat the type as a binding contributor and does not
 * process the methods as injector outputs.</p>
 *
 * @see ActAsStingComponent
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface InjectorFragment
{
}
