package sting.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

final class StingElementsUtil
{
  private StingElementsUtil()
  {
  }

  //TODO: Move to Proton
  static boolean isEffectivelyPublic( @Nonnull final TypeElement element )
  {
    if ( !element.getModifiers().contains( Modifier.PUBLIC ) )
    {
      return false;
    }
    else
    {
      final Element enclosing = element.getEnclosingElement();
      return ElementKind.PACKAGE == enclosing.getKind() || isEffectivelyPublic( (TypeElement) enclosing );
    }
  }

  //TODO: Move to Proton
  static boolean isElementDeprecated( @Nonnull final Element element )
  {
    return element
      .getAnnotationMirrors()
      .stream()
      .anyMatch( a -> a.getAnnotationType().toString().equals( Deprecated.class.getName() ) );
  }
}
