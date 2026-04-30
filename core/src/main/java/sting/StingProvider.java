package sting;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Annotation that indicates the class that provides the service.
 * This is an integration meta-annotation rather than a component-defining Sting annotation.
 *
 * <p>The class that provides the service may be either a {@link Fragment}-annotated type or an
 * {@link Injectable}-annotated type. This annotation is applied to another framework's annotation to
 * simplify integration with Sting. Sting consults it when the framework annotation is applied to a
 * type element that Sting attempts to include or discover.</p>
 *
 * <p>When used for explicit include aliasing, Sting resolves the framework-managed type in an
 * {@link Injector#includes()} annotation parameter or a {@link Fragment#includes()} annotation
 * parameter to the provider type. In this case, the resolved provider only needs to exist and be
 * annotated with {@link Fragment} or {@link Injectable}. The provider does not need to publish the
 * framework-managed type unless that type must later be resolved as a Sting service.</p>
 *
 * <p>When used for auto-discovery, Sting resolves an unresolved service request for the
 * framework-managed type to the provider type. In this case, the resolved provider must be
 * annotated with {@link Fragment} or {@link Injectable} and publish the framework-managed type
 * using the default qualifier.</p>
 *
 * <p>Frameworks that synthesize Sting providers should remember that Sting only observes
 * {@link Eager}, {@link Named}, and {@link Typed} on the resolved provider. Providers used only as
 * explicit include aliases usually do not need to copy those annotations for the framework-managed
 * type. Providers intended to support auto-discovery usually should copy them when they need Sting
 * to treat the framework-managed type as published with those semantics. In practice this means
 * copying the annotations onto the generated {@link Injectable} subtype, or onto the provider
 * method declared by the generated {@link Fragment}, depending on the provider style.</p>
 *
 * <p>The presence of this annotation does not itself create a Sting binding; the resolved provider
 * type is what Sting processes.</p>
 *
 * <p>It should be noted that Sting will attempt to use any annotation with this name and shape so
 * that frameworks do not need a direct code dependency on Sting.</p>
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
