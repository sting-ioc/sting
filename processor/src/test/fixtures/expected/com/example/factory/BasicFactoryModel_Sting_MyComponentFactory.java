package com.example.factory;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(BasicFactoryModel.MyComponentFactory.class)
@Generated("sting.processor.StingProcessor")
public final class BasicFactoryModel_Sting_MyComponentFactory implements BasicFactoryModel.MyComponentFactory {
  @Nonnull
  private final BasicFactoryModel.SomeService $sting$_someService;

  BasicFactoryModel_Sting_MyComponentFactory(
      @Nonnull final BasicFactoryModel.SomeService someService) {
    $sting$_someService = someService;
  }

  @Override
  @Nonnull
  public BasicFactoryModel.MyComponent create(int someParameter) {
    return new BasicFactoryModel.MyComponent($sting$_someService, someParameter);
  }
}
