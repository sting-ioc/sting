package sting.processor;

import java.util.List;
import javax.annotation.Nonnull;
import sting.processor.spi.InterceptedMethodModel;

record InterceptedMethodModelImpl(@Nonnull String methodName, @Nonnull String returnTypeName,
                                  @Nonnull List<String> parameterTypeNames, @Nonnull List<String> thrownTypeNames,
                                  boolean defaultMethod, boolean varArgs)
  implements InterceptedMethodModel
{
}
