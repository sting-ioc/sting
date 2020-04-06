package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation applied to type-targeting annotations that suppresses warnings due to usages of the {@link sting.Named @Named} and {@link sting.ContributeTo @ContributeTo} annotations.
 *
 * <p>Normally a type that is annotated with the {@link sting.Named @Named} annotation, the
 * {@link sting.ContributeTo @ContributeTo} annotation or a type that has a constructor parameter annotated with the
 * {@link sting.Named @Named} annotation will generate a warning if the Sting annotation processor
 * detects that the type is not annotated with the {@link sting.Injectable @Injectable} annotation or an annotation
 * annotated by a {@link sting.StingProvider @StingProvider} annotated annotation. This warning is generated because
 * the annotation processor will not process the annotation and thus considers it a potential error.</p>
 *
 * <p>The @ActAsStingComponent can be applied to an annotation and any type that is annotated with that annotation
 * will not generated warnings in these scenarios. The expectation is that another annotation processor will process
 * the type and will make use of the {@link sting.Named @Named} annotations and/or the
 * {@link sting.ContributeTo @ContributeTo} annotation if present. This allows other frameworks to define their
 * own component model that is fully integration with sting.</p>
 *
 * <p>It should be noted that Sting will attempt to use any annotation with this name and shape so that
 * frameworks do not need a direct code dependency on Sting. </p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface ActAsStingComponent
{
}
