package com.example.fragment.includes.local_only_disabled;

import sting.Fragment;

@Fragment( localOnly = false, includes = com.example.fragment.includes.local_only_disabled.other.MyModel.class )
public interface CrossPackageIncludesModel
{
}
