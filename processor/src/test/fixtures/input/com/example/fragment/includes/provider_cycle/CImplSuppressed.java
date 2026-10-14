package com.example.fragment.includes.provider_cycle;

import sting.Fragment;

@SuppressWarnings("Sting:FragmentIncludeCycle")
@Fragment(includes = A.class)
@org.jspecify.annotations.NullMarked
public interface CImplSuppressed {}
