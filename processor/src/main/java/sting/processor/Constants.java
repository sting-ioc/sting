package sting.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String DEPENDENCY_CLASSNAME = "sting.Qualifier";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

  private Constants()
  {
  }
}
