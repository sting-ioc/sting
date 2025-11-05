package com.example.fragment.includes.provider_cycle;

import sting.Fragment;

@SuppressWarnings( "Sting:FragmentIncludeCycle" )
@Fragment( includes = B.class )
public interface AImplSuppressed {}
