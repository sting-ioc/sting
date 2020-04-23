package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate an interface to declare that it will be used in injector to suppress warnings due to usages of the {@link sting.Named @Named} annotation.
 *
 * <p>Normally a type that has a method annotated with the {@link sting.Named @Named} annotation will
 * generate a warning if the Sting annotation processor detects that the type is not annotated with the
 * {@link sting.Injector @Injector} annotation. This warning is generated because the annotation processor
 * will not process the annotation and thus considers it an error.</p>
 *
 * @see ActAsStingComponent
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface InjectorFragment
{
}
