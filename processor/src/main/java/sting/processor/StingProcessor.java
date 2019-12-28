package sting.processor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import org.realityforge.proton.AbstractStandardProcessor;
import org.realityforge.proton.ElementsUtil;
import org.realityforge.proton.MemberChecks;
import org.realityforge.proton.ProcessorException;

/**
 * Annotation processor that analyzes sting annotated source and generates dependency injection container.
 */
@SupportedAnnotationTypes( Constants.INJECT_CLASSNAME )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
@SupportedOptions( { "sting.defer.unresolved", "sting.defer.errors" } )
public final class StingProcessor
  extends AbstractStandardProcessor
{
  @Nonnull
  @Override
  protected String getIssueTrackerURL()
  {
    return "https://github.com/realityforge/sting/issues";
  }

  @Nonnull
  @Override
  protected String getOptionPrefix()
  {
    return "sting";
  }

  @Nonnull
  @Override
  protected Collection<TypeElement> getTypeElementsToProcess( @Nonnull final RoundEnvironment env )
  {
    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( Constants.INJECT_CLASSNAME );
    final Set<? extends Element> injectElements = env.getElementsAnnotatedWith( annotation );
    final Set<TypeElement> typeElements = new HashSet<>();
    for ( final Element element : injectElements )
    {
      if ( ElementKind.CONSTRUCTOR != element.getKind() )
      {
        reportError( env, "Sting does not support adding the @Inject annotation except on constructors", element );
      }
      else
      {
        typeElements.add( (TypeElement) element.getEnclosingElement() );
      }
    }
    return typeElements;
  }

  protected void process( @Nonnull final TypeElement element )
    throws IOException, ProcessorException
  {
    // Must be a class because we have already found a constructor by the time we get here
    assert ElementKind.CLASS == element.getKind();
    final List<ExecutableElement> constructors = ElementsUtil.getConstructors( element );

    // As can only have got here if we have at least one constructor with @Inject
    assert !constructors.isEmpty();

    final ExecutableElement constructor = constructors.get( 0 );
    if ( constructors.size() > 1 )
    {
      throw new ProcessorException( "@Inject must not appear on a type that contains multiple constructors",
                                    constructor );
    }
    MemberChecks.shouldNotBeProtected( processingEnv,
                                       constructor,
                                       Constants.INJECT_CLASSNAME,
                                       Constants.WARNING_PROTECTED_CONSTRUCTOR,
                                       null );
    MemberChecks.shouldNotBePublic( processingEnv,
                                    constructor,
                                    Constants.INJECT_CLASSNAME,
                                    Constants.WARNING_PUBLIC_CONSTRUCTOR,
                                    null );
  }
}
