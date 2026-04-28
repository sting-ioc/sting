package com.example.factory;

import javax.annotation.Nonnull;
import javax.annotation.processing.Generated;
import sting.Injectable;
import sting.Typed;

@Injectable
@Typed(MultiMethodFactoryModel.ModelFactory.class)
@Generated("sting.processor.StingProcessor")
public final class MultiMethodFactoryModel_Sting_ModelFactory implements MultiMethodFactoryModel.ModelFactory {
  @Nonnull
  private final MultiMethodFactoryModel.SomeService $sting$_someService;

  @Nonnull
  private final MultiMethodFactoryModel.OtherService $sting$_otherService;

  MultiMethodFactoryModel_Sting_ModelFactory(
      @Nonnull final MultiMethodFactoryModel.SomeService someService,
      @Nonnull final MultiMethodFactoryModel.OtherService otherService) {
    $sting$_someService = someService;
    $sting$_otherService = otherService;
  }

  @Override
  @Nonnull
  public MultiMethodFactoryModel.Widget createWidget(int count) {
    return new MultiMethodFactoryModel.Widget($sting$_someService, count);
  }

  @Override
  @Nonnull
  public MultiMethodFactoryModel.Gadget createGadget(@Nonnull String name) {
    return new MultiMethodFactoryModel.Gadget($sting$_someService, $sting$_otherService, name);
  }
}
