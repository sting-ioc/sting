package com.example.integration.todomvc.model;

import sting.Eager;
import sting.Fragment;

@Fragment
@org.jspecify.annotations.NullMarked
public interface BrowserLocationFragment {
    @Eager
    default BrowserLocation createBrowserLocation() {
        return new Arez_BrowserLocation();
    }
}
