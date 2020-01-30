package com.example.injector.inputs;

import java.util.Collection;
import sting.Injector;

@Injector
public interface WildcardCollectionInputModel
{
  Collection<?> getMyThing();
}
