package com.example.factory;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(BasicFactoryModel.MyComponentFactory.class)
@NullMarked
@Generated("sting.processor.StingProcessor")
public final class BasicFactoryModel_Sting_MyComponentFactory implements BasicFactoryModel.MyComponentFactory {
  private final BasicFactoryModel.SomeService $sting$_someService;

  BasicFactoryModel_Sting_MyComponentFactory(final BasicFactoryModel.SomeService someService) {
    $sting$_someService = someService;
  }

  @Override
  public BasicFactoryModel.MyComponent create(int someParameter) {
    return new BasicFactoryModel.MyComponent($sting$_someService, someParameter);
  }
}
