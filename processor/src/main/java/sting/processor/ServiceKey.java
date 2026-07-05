package sting.processor;

import java.util.Map;
import java.util.Objects;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.jspecify.annotations.Nullable;

/**
 * A normalized service key used for matching requests to providers.
 */
final class ServiceKey {
    private static final Map<TypeKind, String> BOXED_TYPES = Map.of(
            TypeKind.BOOLEAN, "java.lang.Boolean",
            TypeKind.BYTE, "java.lang.Byte",
            TypeKind.CHAR, "java.lang.Character",
            TypeKind.DOUBLE, "java.lang.Double",
            TypeKind.FLOAT, "java.lang.Float",
            TypeKind.INT, "java.lang.Integer",
            TypeKind.LONG, "java.lang.Long",
            TypeKind.SHORT, "java.lang.Short");
    private final String _qualifier;
    private final String _type;

    ServiceKey(final Coordinate coordinate) {
        _qualifier = coordinate.qualifier();
        _type = normalizeType(coordinate.type());
    }

    private static String normalizeType(final TypeMirror type) {
        return BOXED_TYPES.getOrDefault(type.getKind(), type.toString());
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        return o instanceof ServiceKey key && matches(_qualifier, _type, key._qualifier, key._type);
    }

    private static boolean matches(
            final String lhsQualifier, final String lhsType, final String rhsQualifier, final String rhsType) {
        return lhsQualifier.equals(rhsQualifier) && lhsType.equals(rhsType);
    }

    static boolean matches(final Coordinate lhsCoordinate, final Coordinate rhsCoordinate) {
        return matches(
                lhsCoordinate.qualifier(), normalizeType(lhsCoordinate.type()),
                rhsCoordinate.qualifier(), normalizeType(rhsCoordinate.type()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(_qualifier, _type);
    }
}
