package sting.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String MODULE_CLASSNAME = "sting.Module";
  @Nonnull
  static final String INJECTOR_CLASSNAME = "sting.Injector";
  @Nonnull
  static final String DEPENDENCY_CLASSNAME = "sting.Dependency";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

  private Constants()
  {
  }
}
