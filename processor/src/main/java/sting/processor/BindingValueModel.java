package sting.processor;

import org.jspecify.annotations.Nullable;

record BindingValueModel(
        String name,
        BindingValueKind kind,
        boolean array,
        @Nullable Object scalarValue,
        @Nullable String className,
        @Nullable String enumTypeName,
        @Nullable String enumConstantName,
        String javaLiteral) {
    public String javaLiteral() {
        if (BindingValueKind.UNSUPPORTED == kind) {
            throw new IllegalStateException("Unsupported binding value " + name + " has no Java literal");
        }
        return javaLiteral;
    }
}
