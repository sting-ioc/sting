package sting.processor;

import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;

/**
 * The mechanism for identifying a service.
 *
 * @param qualifier An opaque string used to restrict the services that match a coordinate.
 * @param type      The java type of the service.
 */
record Coordinate(String qualifier, TypeMirror type) {

    @Override
    public String toString() {
        return "[" + type() + (qualifier().isEmpty() ? "" : ";qualifier='" + qualifier() + "'") + "]";
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return o instanceof Coordinate coordinate
                && qualifier.equals(coordinate.qualifier)
                && type.toString().equals(coordinate.type.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifier, type.toString());
    }
}
