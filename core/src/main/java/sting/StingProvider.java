package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation that indicates the class that provides the service.
 * The class that provides the service may be either a {@link Fragment} annotated type or an {@link Injectable}
 * annotated type. This annotation is applied to another frameworks' annotation to simplify integration with Sting.
 * Sting will process this annotation when the framework annotation is applied to a type element and that Sting
 * attempts to include that type. Sting can attempt to include the type either by the type being added to an
 * {@link Injector#includes()} annotation parameter, a {@link Fragment#includes()} annotation parameter, or by
 * being an unresolved service referenced in component graph that sting attempts to autodetect.
 *
 * <p>It should be noted that Sting will attempt to use any annotation with this name and shape so that
 * frameworks do not need a direct code dependency on Sting. </p>
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface StingProvider
{
  /**
   * The name pattern of the class that provides the service.
   * The name is relative to the reference type (See above for how to determine the reference type).
   * The package is the same package as the reference type. The pattern can include constant string
   * parts as well as the following replacements:
   *
   * <ul>
   *   <li>
   *     <b>[SimpleName]</b>: The simple name of the class. i.e. For a top-level class like
   *     {@code come.example.MyElement} the simple name is {@code "MyElement"}. For a nested class like
   *     {@code come.example.MyElement.ElementType.Kind} the simple name is {@code "Kind"}.
   *   </li>
   *   <li>
   *     <b>[CompoundName]</b>: The compound name of the class. i.e. For a top-level class like
   *     {@code come.example.MyElement} the compound name is {@code "MyElement"}. For a nested class like
   *     {@code come.example.MyElement.ElementType.Kind} the simple name is {@code "MyElement.ElementType.Kind"}.
   *   </li>
   *   <li>
   *     <b>[EnclosingName]</b>: The compound name of the class with the simple name elided. i.e. For a top-level
   *     class like {@code come.example.MyElement} the enclosing name is {@code ""}. For a nested class like
   *     {@code come.example.MyElement.ElementType.Kind} the enclosing name is {@code "MyElement.ElementType."}.
   *   </li>
   *   <li>
   *     <b>[FlatEnclosingName]</b>: The enclosing name of the class with the dots replaced with underscores.
   *     i.e. For a top-level class like {@code come.example.MyElement} the flat enclosing name is {@code ""}.
   *     For a nested class like {@code come.example.MyElement.ElementType.Kind} the flat enclosing name is
   *     {@code "MyElement_ElementType_"}.
   *   </li>
   * </ul>
   *
   * <p>A typical pattern used by a framework such as <a href="https://arez.github.io/">Arez</a> is
   * "[FlatEnclosingName]Arez_[SimpleName]"..</p>
   *
   * @return the pattern to produce a name.
   */
  @Nonnull
  String value();
}
