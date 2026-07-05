package sting.processor;

import javax.lang.model.element.AnnotationMirror;

record ProviderEntry(AnnotationMirror annotation, AnnotationMirror provider) {}
