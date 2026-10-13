package com.example.fragment.qualifier;

import sting.Fragment;
import sting.Named;

@Fragment
@org.jspecify.annotations.NullMarked
public interface NonStandardQualifierModel {
    @Named("\u200E\uD83C\uDF89 Tada!")
    default Runnable provideRunnable() {
        throw new UnsupportedOperationException();
    }
}
