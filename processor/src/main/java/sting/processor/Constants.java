package sting.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String FRAGMENT_CLASSNAME = "sting.Fragment";
  @Nonnull
  static final String FACTORY_CLASSNAME = "sting.Factory";
  @Nonnull
  static final String INJECTOR_CLASSNAME = "sting.Injector";
  @Nonnull
  static final String DEPENDENCY_CLASSNAME = "sting.Dependency";
  @Nonnull
  static final String PROVIDES_CLASSNAME = "sting.Provides";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

  private Constants()
  {
  }
}
