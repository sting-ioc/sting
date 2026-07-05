package sting.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import org.jspecify.annotations.Nullable;
import org.realityforge.proton.ProcessorException;

final class CircularDependencyChecker {
    private CircularDependencyChecker() {}

    static void verifyNoCircularDependencyLoops(final ComponentGraph graph) {
        final var completed = new HashSet<Node>();

        verifyNoCircularDependenciesForRootNode(graph, graph.getRootNode(), completed);

        for (final Node node : graph.getNodes()) {
            if (node.getUsedBy().isEmpty()) {
                verifyNoCircularDependenciesForRootNode(graph, node, completed);
            }
        }
    }

    private static void verifyNoCircularDependenciesForRootNode(
            final ComponentGraph graph, final Node node, final Set<Node> completed) {
        final var stack = new Stack<PathEntry>();
        final var entry = new PathEntry(node, null);
        verifyNoCircularDependenciesForNode(graph, entry, stack, completed);
        assert stack.isEmpty();
    }

    private static void verifyNoCircularDependenciesForNode(
            final ComponentGraph graph,
            final PathEntry entry,
            final Stack<PathEntry> stack,
            final Set<Node> completed) {
        stack.add(entry);
        for (final Edge edge : entry.node().getDependsOn()) {
            for (final Node node : edge.getSatisfiedBy()) {
                final var childEntry = new PathEntry(node, edge);
                final int indexOfMatching =
                        doesEdgeBreakDependencyChain(edge) ? -1 : detectCircularDependency(stack, node);
                if (-1 != indexOfMatching) {
                    throw new ProcessorException(
                            "Injector contains a circular dependency.\n" + "Path:\n"
                                    + describeCircularDependencyPath(stack, childEntry),
                            graph.getInjector().getElement());
                } else {
                    if (!completed.contains(node)) {
                        completed.add(entry.node());

                        final int size = stack.size();
                        verifyNoCircularDependenciesForNode(graph, childEntry, stack, completed);
                        assert size == stack.size();
                    }
                }
            }
        }
        stack.pop();
    }

    private static int detectCircularDependency(final Stack<PathEntry> stack, final Node node) {
        int index = stack.size() - 1;
        while (index > 0) {
            final PathEntry entry = stack.get(index);
            if (doesEdgeBreakDependencyChain(entry.edge())) {
                return -1;
            } else if (entry.node() == node) {
                return index - 1;
            } else {
                index--;
            }
        }
        return -1;
    }

    /**
     * Return true if circular dependency check does not need to check backwards over edge.
     *
     * @param edge the edge.
     * @return true if edge means dependency chekcing can cease.
     */
    private static boolean doesEdgeBreakDependencyChain(@Nullable final Edge edge) {
        return null == edge || edge.getServiceRequest().getKind().isSupplier();
    }

    /**
     * Generate a description of the dependency stack that includes a circular dependency.
     *
     * @param stack    the dependency stack.
     * @param badEntry the entry that depends upon itself.
     * @return a string description.
     */
    private static String describeCircularDependencyPath(final Stack<PathEntry> stack, final PathEntry badEntry) {
        final var sb = new StringBuilder();

        boolean matched = false;
        for (final PathEntry entry : stack) {
            final Node node = entry.node();
            final String connector;
            if (node == badEntry.node()) {
                connector = "+-<";
                matched = true;
            } else if (matched) {
                connector = "|  ";
            } else {
                connector = "   ";
            }
            sb.append(node.describe(connector));

            sb.append("\n");
        }

        sb.append(badEntry.node().describe("+->"));
        sb.append("\n");

        return sb.toString();
    }
}
