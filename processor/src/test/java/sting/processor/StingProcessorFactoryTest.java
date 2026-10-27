package sting.processor;

import static org.testng.Assert.*;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import org.realityforge.proton.qa.Compilation;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class StingProcessorFactoryTest extends AbstractStingProcessorTest {
    private static final String PACKAGE = "com.example.factory.providers";
    private static final String IMPLEMENTATION = """
        final class ComponentImpl extends Component {
            private final Service service;
            private final int count;
            private final String label;

            ComponentImpl(Service service, int count, @Nullable String label) {
                this.service = service;
                this.count = count;
                this.label = label;
            }

            public Service service() { return service; }
            public int count() { return count; }
            public String label() { return label; }
        }
        """;

    @DataProvider
    public Object[][] declarationKinds() {
        return new Object[][] {{"abstract class"}, {"interface"}, {"class"}};
    }

    @Test(dataProvider = "declarationKinds")
    public void constructsMappedImplementation(final String kind) throws Exception {
        final List<JavaFileObject> sources = componentSources(kind);
        sources.add(source("ComponentImpl", implementation(kind)));
        final Compilation compilation = compile(sources);
        assertCompilationSuccessful(compilation);
        assertGeneratedConstruction(compilation);
        runHarness(compilation);
    }

    @DataProvider
    public Object[][] invalidImplementations() {
        return new Object[][] {
            {"interface ComponentImpl {}", "create a concrete class"},
            {"abstract class ComponentImpl extends Component {}", "create a concrete class"},
            {"class ComponentImpl {}", "is not assignable to"},
            {"class ComponentImpl<T> extends Component {}", "create a class with type parameters"},
            {
                "class ComponentImpl extends Component { private ComponentImpl(int count) {} }",
                "constructor accessible from the factory interface"
            },
            {
                "class ComponentImpl extends Component { ComponentImpl() {} ComponentImpl(int count) {} }",
                "single accessible constructor"
            },
            {
                "class ComponentImpl extends Component { ComponentImpl(String count) {} }",
                "parameters whose name and type match"
            }
        };
    }

    @Test(dataProvider = "invalidImplementations")
    public void rejectsInvalidImplementation(final String implementation, final String diagnostic) {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentImpl", implementation),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(int count); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, diagnostic);
    }

    @Test
    public void rejectsNonStaticImplementation() {
        final Compilation compilation = compile(List.of(
                framework("Holder.Implementation"),
                source("Component", "@Framework abstract class Component {}"),
                source("Holder", "class Holder { class Implementation extends Component {} }"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "non-static nested class");
    }

    @Test
    public void rejectsMultipleMappings() {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("OtherFramework", "@StingProvider(\"OtherImpl\") @interface OtherFramework {}"),
                source("Component", "@Framework @OtherFramework abstract class Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "multiple @StingProvider annotations");
    }

    @Test
    public void rejectsMalformedMapping() {
        final Compilation compilation = compile(List.of(
                source("Framework", """
                    @interface StingProvider { int value(); }
                    @StingProvider(1) @interface Framework {}
                    """),
                source("Component", "@Framework abstract class Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "invalid @StingProvider annotation");
    }

    @Test(dataProvider = "unmappedDeclarations")
    public void rejectsUnmappedDeclaration(final String declaration, final String diagnostic) {
        final Compilation compilation = compile(List.of(
                source("Component", declaration + " Component {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, diagnostic);
    }

    @DataProvider
    public Object[][] unmappedDeclarations() {
        return new Object[][] {{"abstract class", "abstract class"}, {"interface", "class type"}};
    }

    @Test
    public void rejectsRawGenericDeclarationEvenWithNonGenericImplementation() {
        final Compilation compilation = compile(List.of(
                framework("ComponentImpl"),
                source("Component", "@Framework interface Component<T> {}"),
                source("ComponentImpl", "class ComponentImpl implements Component<String> {}"),
                source("ComponentFactory", "@Factory interface ComponentFactory { Component create(); }")));
        assertFalse(compilation.success());
        assertDiagnosticContaining(compilation, "return a class with type parameters");
    }

    private List<JavaFileObject> componentSources(final String kind) {
        final String declaration = "interface".equals(kind)
                ? """
                @Framework public interface Component {
                    Service service();
                    int count();
                    String label();
                }
                """
                : "@Framework public " + kind + " Component {\n"
                        + "  public Service service() { return null; }\n"
                        + "  public int count() { return -1; }\n"
                        + "  public String label() { return null; }\n"
                        + "}\n";
        return new ArrayList<>(List.of(
                framework("[SimpleName]Impl"),
                source("Component", declaration),
                source("Service", """
                    interface Service {}
                    @Injectable @Typed(Service.class) final class ServiceImpl implements Service {}
                    """),
                source("ComponentFactory", """
                    @Factory public interface ComponentFactory {
                        Component create(int count, @Nullable String label);
                    }
                    """),
                source("Harness", """
                    @sting.Injector(fragmentOnly = false, includes = {ComponentFactory.class, ServiceImpl.class})
                    interface MyInjector { ComponentFactory factory(); }
                    public class Harness {
                        public static void verify() {
                            final var injector = new Sting_MyInjector();
                            final var factory = injector.factory();
                            final var first = factory.create(23, null);
                            final var second = factory.create(42, "second");
                            if (first == second || first.count() != 23 || second.count() != 42
                                || first.label() != null || !"second".equals(second.label())
                                || first.service() == null || first.service() != second.service()
                                || !first.getClass().getSimpleName().equals("ComponentImpl")) {
                                throw new AssertionError("Incorrect factory construction");
                            }
                        }
                    }
                    """)));
    }

    private String implementation(final String kind) {
        return "interface".equals(kind)
                ? IMPLEMENTATION.replace("extends Component", "implements Component")
                : IMPLEMENTATION;
    }

    private JavaFileObject framework(final String pattern) {
        return source("Framework", """
            @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
            @interface StingProvider { String value(); }
            @StingProvider("%s")
            @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
            @interface Framework {}
            """.formatted(pattern));
    }

    private JavaFileObject source(final String name, final String content) {
        final String qualifiedName = PACKAGE + "." + name;
        final String text = "package " + PACKAGE + ";\nimport sting.*;\nimport org.jspecify.annotations.Nullable;\n" + content;
        return new SimpleJavaFileObject(
                URI.create("string:///" + qualifiedName.replace('.', '/') + ".java"), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(final boolean ignoreEncodingErrors) {
                return text;
            }
        };
    }

    private void assertGeneratedConstruction(final Compilation compilation) throws Exception {
        final String generated = Files.readString(
                compilation.sourceOutput().resolve(PACKAGE.replace('.', '/') + "/Sting_ComponentFactory.java"));
        assertTrue(generated.contains("Component create("), generated);
        assertTrue(generated.contains("new ComponentImpl("), generated);
        assertFalse(generated.contains("new Component("), generated);
    }

    private void runHarness(final Compilation compilation) throws Exception {
        try (final var loader = new URLClassLoader(
                new URL[] {compilation.classOutput().toUri().toURL()},
                getClass().getClassLoader())) {
            loader.loadClass(PACKAGE + ".Harness").getMethod("verify").invoke(null);
        }
    }

    private void assertDiagnosticContaining(final Compilation compilation, final String text) {
        final String diagnostics = compilation.diagnostics().toString();
        assertTrue(diagnostics.contains(text), diagnostics);
    }
}
