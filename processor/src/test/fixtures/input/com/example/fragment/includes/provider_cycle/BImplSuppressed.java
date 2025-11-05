package com.example.fragment.includes.provider_cycle;

import sting.Fragment;

@SuppressWarnings( "Sting:FragmentIncludeCycle" )
@Fragment( includes = CImplSuppressed.class )
public interface BImplSuppressed {}
