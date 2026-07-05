package sting.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

final class InjectorDotReportGenerator {
    private InjectorDotReportGenerator() {}

    static String buildDotReport(final ProcessingEnvironment processingEnv, final ComponentGraph graph) {
        final Map<String, Set<String>> types = buildTypeMap(graph);

        final var sb = new StringBuilder();
        final String injectorName = extractShortestUniqueName(
                types, graph.getInjector().getElement().asType().toString());
        sb.append("digraph \"")
                .append(injectorName)
                .append("\" {\n")
                .append("  overlap = false\n")
                .append("  splines = true\n");

        sb.append("  injector [label=\"").append(injectorName).append("\",color=green];\n");

        for (final Node node : graph.getRawNodeCollection()) {
            sb.append("  ")
                    .append(node.getName())
                    .append(" [label=\"")
                    .append(node.isProxy() ? "Proxy " : "")
                    .append(extractShortestUniqueName(types, node.getType().toString()));

            final List<ServiceSpec> services = node.getProviderBinding().getPublishedServices();
            if (!services.isEmpty()) {
                final String qualifier;
                if (node.isProxy()) {
                    qualifier = node.getProxy()
                            .getService()
                            .service()
                            .getCoordinate()
                            .qualifier();
                } else {
                    qualifier = services.get(0).getCoordinate().qualifier();
                }
                if (!qualifier.isEmpty()) {
                    sb.append("/").append(qualifier);
                }
            }
            if (node.isProxy()) {
                sb.append("\\n").append(node.getProxy().getId());
            }

            sb.append("\"");
            if (node.isEager()) {
                sb.append(",color=blue");
            }
            sb.append("];\n");
        }

        emitDependencyLinks(processingEnv, types, sb, graph.getRootNode(), "injector");
        for (final Node node : graph.getRawNodeCollection()) {
            emitDependencyLinks(processingEnv, types, sb, node, node.getName());
        }
        sb.append("}\n");
        return sb.toString();
    }

    private static void emitDependencyLinks(
            final ProcessingEnvironment processingEnv,
            final Map<String, Set<String>> types,
            final StringBuilder sb,
            final Node node,
            final String fromName) {
        for (final Edge edge : node.getDependsOn()) {
            emitNodeLinks(processingEnv, types, sb, edge, fromName);
        }
    }

    private static void emitNodeLinks(
            final ProcessingEnvironment processingEnv,
            final Map<String, Set<String>> types,
            final StringBuilder sb,
            final Edge edge,
            final String fromName) {
        for (final Node other : edge.getSatisfiedBy()) {
            emitNodeLink(processingEnv, types, sb, edge, other, fromName);
        }
    }

    private static void emitNodeLink(
            final ProcessingEnvironment processingEnv,
            final Map<String, Set<String>> types,
            final StringBuilder sb,
            final Edge edge,
            final Node toNode,
            final String fromName) {
        sb.append("  ").append(fromName).append(" -> ").append(toNode.getName()).append(" [");
        boolean hasAttributes = false;
        final ServiceRequest serviceRequest = edge.getServiceRequest();
        final ServiceSpec service = serviceRequest.getService();
        final Coordinate coordinate = service.getCoordinate();
        final TypeMirror serviceType = coordinate.type();
        if (!processingEnv.getTypeUtils().isSameType(serviceType, toNode.getType())) {
            sb.append("label=\"").append(extractShortestUniqueName(types, serviceType.toString()));
            final String qualifier = coordinate.qualifier();
            if (!qualifier.isEmpty()) {
                sb.append("/").append(qualifier);
            }
            sb.append("\"");
            hasAttributes = true;
        }
        if (serviceRequest.isOptionalLike()) {
            if (hasAttributes) {
                sb.append(",");
            }
            sb.append("style=dotted");
            hasAttributes = true;
        }
        final ServiceRequest.Kind kind = serviceRequest.getKind();
        if (kind.isCollection() && kind.isSupplier()) {
            if (hasAttributes) {
                sb.append(",");
            }
            sb.append("dir=both, arrowtail=odot, arrowhead=crow");
        } else if (kind.isCollection()) {
            if (hasAttributes) {
                sb.append(",");
            }
            sb.append("dir=both, arrowtail=normal, arrowhead=crow");
        } else if (kind.isSupplier()) {
            if (hasAttributes) {
                sb.append(",");
            }
            sb.append("arrowhead=odot");
        }
        sb.append("];\n");
    }

    private static Map<String, Set<String>> buildTypeMap(final ComponentGraph graph) {
        // Map used to try and generate the shortest name for a node
        // SimpleName -> [FQN]
        final var types = new HashMap<String, Set<String>>();
        for (final Node node : graph.getRawNodeCollection()) {
            recordType(types, node.getType().toString());
            recordDependencyTypes(types, node);
        }
        recordType(types, graph.getInjector().getElement().asType().toString());
        recordDependencyTypes(types, graph.getRootNode());
        return types;
    }

    private static void recordDependencyTypes(final Map<String, Set<String>> types, final Node node) {
        for (final Edge edge : node.getDependsOn()) {
            recordType(
                    types,
                    edge.getServiceRequest().getService().getCoordinate().type().toString());
        }
    }

    private static void recordType(final Map<String, Set<String>> types, final String type) {
        types.computeIfAbsent(extractSimpleName(type), v -> new HashSet<>()).add(type);
    }

    private static String extractShortestUniqueName(final Map<String, Set<String>> types, final String typeName) {
        final String simpleName = extractSimpleName(typeName);
        final Set<String> matches = Objects.requireNonNull(types.get(simpleName));
        if (1 == matches.size()) {
            return simpleName;
        } else {
            final var parts = new ArrayList<String>();
            parts.add(simpleName);

            boolean matched = true;
            int offset = 1;
            while (matched) {
                String match = null;
                for (final String type : matches) {
                    final String[] typeParts = type.split("\\.");
                    if (typeParts.length <= offset) {
                        match = null;
                        matched = false;
                        break;
                    } else if (null == match) {
                        match = typeParts[typeParts.length - 1 - offset];
                    } else if (!match.equals(typeParts[typeParts.length - 1 - offset])) {
                        match = null;
                        matched = false;
                        break;
                    }
                }
                if (null != match) {
                    parts.add(0, match);
                    offset++;
                }
            }
            final String[] typeParts = typeName.split("\\.");
            parts.add(0, typeParts[typeParts.length - 1 - offset]);
            return "..." + String.join(".", parts);
        }
    }

    private static String extractSimpleName(final String type) {
        final String[] parts = type.split("\\.");
        return parts[parts.length - 1];
    }
}
