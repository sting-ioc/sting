package sting.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;

record ProviderEntry(@Nonnull AnnotationMirror annotation, @Nonnull AnnotationMirror provider)
{
}
