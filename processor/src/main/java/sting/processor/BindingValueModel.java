package sting.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Compile-time scalar value from an interceptor binding annotation member.
 */
public interface BindingValueModel
{
  /**
   * Return the annotation member name.
   *
   * @return the annotation member name.
   */
  @Nonnull
  String name();

  /**
   * Return the value kind.
   *
   * @return the value kind.
   */
  @Nonnull
  BindingValueKind kind();

  /**
   * Return the boxed scalar value for primitive and string kinds.
   *
   * @return the boxed scalar value, or null when this value uses kind-specific metadata.
   */
  @Nullable
  Object scalarValue();

  /**
   * Return the fully qualified class name for class-valued members.
   *
   * @return the fully qualified class name, or null when the kind is not {@link BindingValueKind#CLASS}.
   */
  @Nullable
  String className();

  /**
   * Return the fully qualified enum type name for enum-valued members.
   *
   * @return the fully qualified enum type name, or null when the kind is not {@link BindingValueKind#ENUM}.
   */
  @Nullable
  String enumTypeName();

  /**
   * Return the enum constant name for enum-valued members.
   *
   * @return the enum constant name, or null when the kind is not {@link BindingValueKind#ENUM}.
   */
  @Nullable
  String enumConstantName();

  /**
   * Return a Java source literal for the value.
   *
   * @return a Java source literal for supported values.
   * @throws IllegalStateException if the value kind is unsupported.
   */
  @Nonnull
  String javaLiteral();
}
