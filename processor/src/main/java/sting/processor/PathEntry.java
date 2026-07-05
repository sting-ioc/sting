package sting.processor;

import org.jspecify.annotations.Nullable;

record PathEntry(Node node, @Nullable Edge edge) {
    PathEntry {
        assert null != edge || node.hasNoBinding() || node.isEager();
    }
}
