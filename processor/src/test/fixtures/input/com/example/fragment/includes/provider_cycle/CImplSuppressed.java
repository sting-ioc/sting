package com.example.fragment.includes.provider_cycle;

import sting.Fragment;

@SuppressWarnings( "Sting:FragmentIncludeCycle" )
@Fragment( includes = A.class )
public interface CImplSuppressed {}

