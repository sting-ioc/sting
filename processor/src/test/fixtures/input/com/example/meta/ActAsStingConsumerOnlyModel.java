package com.example.meta;

import sting.ActAsStingConsumer;

public final class ActAsStingConsumerOnlyModel
{
  @ActAsStingConsumer
  @interface FrameworkComponent
  {
  }

  private ActAsStingConsumerOnlyModel()
  {
  }
}
