package com.example.fragment.includes.redundant_injectable;

import sting.Fragment;

@SuppressWarnings({"Sting:AutoDiscoverableIncluded", "Sting:RedundantExplicitInjectableInclude"})
@Fragment(includes = {MyFragment.class, MyModel.class})
@org.jspecify.annotations.NullMarked
public interface SuppressedRedundantInjectableInFragmentModel {}
