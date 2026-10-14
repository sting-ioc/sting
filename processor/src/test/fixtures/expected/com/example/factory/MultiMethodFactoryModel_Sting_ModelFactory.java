package com.example.factory;

import javax.annotation.processing.Generated;
import org.jspecify.annotations.NullMarked;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(MultiMethodFactoryModel.ModelFactory.class)
@NullMarked
@Generated("sting.processor.StingProcessor")
public final class MultiMethodFactoryModel_Sting_ModelFactory implements MultiMethodFactoryModel.ModelFactory {
  private final MultiMethodFactoryModel.SomeService $sting$_someService;

  private final MultiMethodFactoryModel.OtherService $sting$_otherService;

  MultiMethodFactoryModel_Sting_ModelFactory(final MultiMethodFactoryModel.SomeService someService,
      final MultiMethodFactoryModel.OtherService otherService) {
    $sting$_someService = someService;
    $sting$_otherService = otherService;
  }

  @Override
  public MultiMethodFactoryModel.Widget createWidget(int count) {
    return new MultiMethodFactoryModel.Widget($sting$_someService, count);
  }

  @Override
  public MultiMethodFactoryModel.Gadget createGadget(String name) {
    return new MultiMethodFactoryModel.Gadget($sting$_someService, $sting$_otherService, name);
  }
}
