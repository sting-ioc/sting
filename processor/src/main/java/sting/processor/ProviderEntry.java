package sting.processor;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;

final class ProviderEntry
{
  @Nonnull
  private final AnnotationMirror _annotation;
  @Nonnull
  private final AnnotationMirror _provider;

  ProviderEntry( @Nonnull final AnnotationMirror annotation, @Nonnull final AnnotationMirror provider )
  {
    _annotation = Objects.requireNonNull( annotation );
    _provider = Objects.requireNonNull( provider );
  }

  @Nonnull
  AnnotationMirror getAnnotation()
  {
    return _annotation;
  }

  @Nonnull
  AnnotationMirror getProvider()
  {
    return _provider;
  }
}
