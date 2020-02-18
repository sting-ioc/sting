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
  static final String INPUT_CLASSNAME = "sting.Injector.Input";
  @Nonnull
  static final String NAMED_CLASSNAME = "sting.Named";
  @Nonnull
  static final String JSR_330_NAMED_CLASSNAME = "javax.inject.Named";
  @Nonnull
  static final String JSR_330_INJECT_CLASSNAME = "javax.inject.Inject";
  @Nonnull
  static final String JSR_330_SCOPE_CLASSNAME = "javax.inject.Scope";
  @Nonnull
  static final String EAGER_CLASSNAME = "sting.Eager";
  @Nonnull
  static final String TYPED_CLASSNAME = "sting.Typed";
  @Nonnull
  static final String CDI_TYPED_CLASSNAME = "javax.enterprise.inject.Typed";
  @Nonnull
  static final String WARNING_PROTECTED_CONSTRUCTOR = "Sting:ProtectedConstructor";
  @Nonnull
  static final String WARNING_PUBLIC_CONSTRUCTOR = "Sting:PublicConstructor";
  @Nonnull
  static final String WARNING_JSR_330_NAMED = "Sting:Jsr330NamedPresent";
  @Nonnull
  static final String WARNING_JSR_330_INJECT = "Sting:Jsr330InjectPresent";
  @Nonnull
  static final String WARNING_JSR_330_SCOPED = "Sting:Jsr330ScopedPresent";
  @Nonnull
  static final String WARNING_CDI_TYPED = "Sting:CdiTypedPresent";

  private Constants()
  {
  }
}
