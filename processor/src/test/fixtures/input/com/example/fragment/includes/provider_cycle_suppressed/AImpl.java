package com.example.fragment.includes.provider_cycle_suppressed;

import sting.Fragment;

@SuppressWarnings("Sting:FragmentIncludeCycle")
@Fragment(includes = B.class)
@org.jspecify.annotations.NullMarked
public interface AImpl {}
