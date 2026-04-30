package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation applied to type-targeting annotations that suppress warnings due to usages of
 * {@link sting.Named @Named}. This is an integration meta-annotation rather than a
 * component-defining Sting annotation.
 *
 * <p>When a type is annotated with an annotation meta-annotated by {@link ActAsStingComponent},
 * Sting tolerates {@link sting.Named @Named} on the type and its constructor parameters without
 * treating that type as a Sting-managed component. The expectation is that another annotation
 * processor will process the type and make use of the qualifier information if present.</p>
 *
 * <p>This annotation only affects validation. It does not participate in explicit include
 * resolution, provider-backed auto-discovery, or any other graph construction step.</p>
 *
 * <p>It should be noted that Sting will attempt to use any annotation with this name and shape so
 * that frameworks do not need a direct code dependency on Sting.</p>
 *
 * @see InjectorFragment
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface ActAsStingComponent
{
}
