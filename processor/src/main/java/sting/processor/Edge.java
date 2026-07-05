package sting.processor;

import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

final class Edge {
    /**
     * The node that declared the service request.
     */
    private final Node _node;
    /**
     * The service that has been requested by the node.
     */
    private final ServiceRequest _serviceRequest;
    /**
     * The node(s) used to satisfy the service.
     * May be null if the service is optional and no node exists to satisfy service.
     */
    @Nullable
    private Collection<Node> _satisfiedBy;

    Edge(final Node node, final ServiceRequest serviceRequest) {
        _node = Objects.requireNonNull(node);
        _serviceRequest = Objects.requireNonNull(serviceRequest);
    }

    void setSatisfiedBy(final Collection<Node> satisfiedBy) {
        assert !satisfiedBy.isEmpty() || _serviceRequest.canBeAbsent();
        _satisfiedBy = satisfiedBy;
        for (final Node node : satisfiedBy) {
            node.usedBy(this);
            node.setDepth(Math.min(_node.getDepth() + 1, node.getDepth()));
        }
    }

    boolean isSatisfied() {
        return null != _satisfiedBy;
    }

    ServiceRequest getServiceRequest() {
        return _serviceRequest;
    }

    Collection<Node> getSatisfiedBy() {
        final var satisfiedBy = Objects.requireNonNull(_satisfiedBy);
        assert !satisfiedBy.isEmpty() || _serviceRequest.canBeAbsent();
        return satisfiedBy;
    }
}
