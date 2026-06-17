package sting.processor;

import java.util.List;
import javax.annotation.Nonnull;

record InterceptedMethodModelImpl(@Nonnull String methodName, @Nonnull String returnTypeName,
                                  @Nonnull List<String> parameterTypeNames, @Nonnull List<String> thrownTypeNames,
                                  boolean defaultMethod, boolean varArgs)
  implements InterceptedMethodModel
{
}
