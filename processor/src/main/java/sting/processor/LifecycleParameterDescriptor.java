package sting.processor;

import javax.annotation.Nonnull;

record LifecycleParameterDescriptor(@Nonnull Kind kind, @Nonnull String name)
{
  enum Kind
  {
    SERVICE_TYPE,
    METHOD_NAME,
    BINDING_VALUE,
    ARGUMENTS,
    RESULT,
    THROWN
  }
}
