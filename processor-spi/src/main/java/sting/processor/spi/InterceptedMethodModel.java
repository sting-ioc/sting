package sting.processor.spi;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Immutable metadata for one intercepted service method.
 */
public interface InterceptedMethodModel
{
  /**
   * Return the intercepted method name.
   *
   * @return the intercepted method name.
   */
  @Nonnull
  String methodName();

  /**
   * Return the erased return type name.
   *
   * @return the erased return type name.
   */
  @Nonnull
  String returnTypeName();

  /**
   * Return the erased parameter type names in declaration order.
   *
   * @return the erased parameter type names in declaration order.
   */
  @Nonnull
  List<String> parameterTypeNames();

  /**
   * Return the declared thrown type names in declaration order.
   *
   * @return the declared thrown type names in declaration order.
   */
  @Nonnull
  List<String> thrownTypeNames();

  /**
   * Return true if the intercepted method is a default interface method.
   *
   * @return true if the intercepted method is a default interface method.
   */
  boolean defaultMethod();

  /**
   * Return true if the intercepted method is variadic.
   *
   * @return true if the intercepted method is variadic.
   */
  boolean varArgs();
}
