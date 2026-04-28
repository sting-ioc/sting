package com.example.factory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.Generated;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(ParameterAnnotationsFactoryModel.MyComponentFactory.class)
@Generated("sting.processor.StingProcessor")
public final class ParameterAnnotationsFactoryModel_Sting_MyComponentFactory implements ParameterAnnotationsFactoryModel.MyComponentFactory {
  @Nonnull
  private final ParameterAnnotationsFactoryModel.SomeService $sting$_someService;

  ParameterAnnotationsFactoryModel_Sting_MyComponentFactory(
      @Nonnull final ParameterAnnotationsFactoryModel.SomeService someService) {
    $sting$_someService = someService;
  }

  @Override
  @Nonnull
  public ParameterAnnotationsFactoryModel.MyComponent create(@Nullable String name,
      @Nonnull Runnable action, int count) {
    return new ParameterAnnotationsFactoryModel.MyComponent($sting$_someService, name, action, count);
  }
}
