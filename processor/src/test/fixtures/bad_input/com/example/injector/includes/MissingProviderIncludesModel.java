package com.example.injector.includes;

import sting.Injector;
import sting.StingProvider;

@Injector( includes = MissingProviderIncludesModel_MyComponent.class )
public interface MissingProviderIncludesModel
{
  MissingProviderIncludesModel_MyComponent getMyComponent();
}

@StingProvider( "[FlatEnclosingName]MF1_[SimpleName]_Provider" )
@interface MissingProviderIncludesModel_MyFramework
{
}

@MissingProviderIncludesModel_MyFramework
class MissingProviderIncludesModel_MyComponent
{
}
