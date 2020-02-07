package sting.processor;

import javax.annotation.Nonnull;

final class Constants
{
  @Nonnull
  static final String INJECTABLE_CLASSNAME = "sting.Injectable";
  @Nonnull
  static final String FRAGMENT_CLASSNAME = "sting.Fragment";
  @Nonnull
  static final String INJECTOR_CLASSNAME = "sting.Injector";
  @Nonnull
  static final String SERVICE_CLASSNAME = "sting.Service";
  @Nonnull
  static final String NAMED_CLASSNAME = "sting.Named";
  @Nonnull
  static final String PROVIDES_CLASSNAME = "sting.Provides";
  @Nonnull
  static final String EAGER_CLASSNAME = "sting.Eager";
  @Nonnull
  static final String TYPED_CLASSNAME = "sting.Typed";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";

  private Constants()
  {
  }
}
