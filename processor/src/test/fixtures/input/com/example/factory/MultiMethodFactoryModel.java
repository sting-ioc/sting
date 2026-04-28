package com.example.factory;

import javax.annotation.Nonnull;
import sting.Factory;
import sting.Injectable;

public final class MultiMethodFactoryModel
{
  @Injectable
  public static class SomeService
  {
  }

  @Injectable
  public static class OtherService
  {
  }

  public static class Widget
  {
    Widget( @Nonnull final SomeService someService, final int count )
    {
    }
  }

  public static class Gadget
  {
    Gadget( @Nonnull final SomeService someService,
            @Nonnull final OtherService otherService,
            @Nonnull final String name )
    {
    }
  }

  @Factory
  public interface ModelFactory
  {
    @Nonnull
    Widget createWidget( int count );

    @Nonnull
    Gadget createGadget( @Nonnull final String name );
  }
}
