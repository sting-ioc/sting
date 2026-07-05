package sting.processor;

import java.util.Objects;
import java.util.Stack;

final class WorkEntry {
    private final PathEntry _entry;
    private final Stack<PathEntry> _stack;

    WorkEntry(final PathEntry entry, final Stack<PathEntry> stack) {
        _entry = Objects.requireNonNull(entry);
        _stack = Objects.requireNonNull(stack);
    }

    PathEntry getEntry() {
        return _entry;
    }

    Stack<PathEntry> getStack() {
        return _stack;
    }

    String describePathFromRoot() {
        final var sb = new StringBuilder();

        final int size = _stack.size();
        for (int i = 0; i < size; i++) {
            final PathEntry entry = _stack.get(i);
            final Node node = entry.node();
            final String connector;
            if (node.hasNoBinding()) {
                connector = "   ";
            } else if (size - 1 == i) {
                connector = " * ";
            } else {
                connector = "   ";
            }
            sb.append(node.describe(connector));

            sb.append("\n");
        }

        return sb.toString();
    }
}
