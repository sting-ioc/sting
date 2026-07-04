package sting.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

record PathEntry(@Nonnull Node node, @Nullable Edge edge) {
    PathEntry {
        assert null != edge || node.hasNoBinding() || node.isEager();
    }
}
