package com.example.factory;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(ParameterAnnotationsFactoryModel.MyComponentFactory.class)
@NullMarked
@Generated("sting.processor.StingProcessor")
public final class ParameterAnnotationsFactoryModel_Sting_MyComponentFactory
    implements ParameterAnnotationsFactoryModel.MyComponentFactory {
  private final ParameterAnnotationsFactoryModel.SomeService $sting$_someService;

  ParameterAnnotationsFactoryModel_Sting_MyComponentFactory(
      final ParameterAnnotationsFactoryModel.SomeService someService) {
    $sting$_someService = someService;
  }

  @Override
  public ParameterAnnotationsFactoryModel.MyComponent create(
      @Nullable String name, Runnable action, int count) {
    return new ParameterAnnotationsFactoryModel.MyComponent(
        $sting$_someService, name, action, count);
  }
}
