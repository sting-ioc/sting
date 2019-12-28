package sting.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String INJECT_CLASSNAME = "javax.inject.Inject";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

  private Constants()
  {
  }
}
