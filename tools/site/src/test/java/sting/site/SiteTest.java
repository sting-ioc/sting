package sting.site;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class SiteTest {
    @Test
    public void rendersDocumentationLinksTablesAndEscapedSources() {
        final var source =
                "package example;\n\n@Injectable\nclass Example {\n  String value = \"<h1>Hello</h1>\";\n}\n";
        final var html = Markdown.render("""
            ## @Typed on @Injectable types

            See {@link: sting.Injector#includes() @Injector.includes} and [Fragments](fragments.md#cycles).
            Keep [GitHub](https://github.com/example/doc.md) unchanged.

            | Key | Value |
            | --- | --- |
            | a | b |

            <figure><img src="/img/example.svg" alt="Example"><figcaption>Caption</figcaption></figure>

            {@file_content: file=example/Example.java start_line=@Injectable}
            """, Map.of("example/Example.java", source));
        Assert.assertTrue(html.contains("id=\"typed-on-injectable-types\""));
        Assert.assertTrue(html.contains("href=\"/api/sting/Injector.html#includes()\""));
        Assert.assertTrue(html.contains("href=\"fragments.html#cycles\""));
        Assert.assertTrue(html.contains("href=\"https://github.com/example/doc.md\""));
        Assert.assertTrue(html.contains("<table>"));
        Assert.assertTrue(html.contains("<figcaption>Caption</figcaption>"));
        Assert.assertTrue(html.contains("&lt;h1&gt;Hello&lt;/h1&gt;"));
        Assert.assertFalse(html.contains("package example"));
    }

    @Test
    public void leavesDirectiveExamplesInsideCodeUntouched() {
        final var html = Markdown.render("""
            Use `{@link: ...}` or `{@file_content: ...}`.

            ```text
            {@link: ...}
            ```

                {@file_content: ...}
            """, Map.of());
        Assert.assertTrue(html.contains("<code>{@link: ...}</code>"));
        Assert.assertTrue(html.contains("{@file_content: ...}"));
    }

    @Test
    public void extractsQuotedBoundariesElidesAndRemovesCommonIndent() {
        final var source = """
            class Example {
                public static void main(String[] args) {
                    before();
                    // DOC ELIDE START
                    hidden();
                    // DOC ELIDE END
                    after();
                }
            }
            """;
        Assert.assertEquals(Markdown.snippet("""
            file=example/Example.java "start_line=public static void main" "end_line=^    }"
            include_start_line=false include_end_line=false
            """, Map.of("example/Example.java", source)), "before();\n...\nafter();");
        Assert.assertEquals(
                Markdown.snippet(
                        "path=generated/processors/main/java file=example/Sting_Example.java"
                                + " start_line=Sting_Example\\( end_line=\\)",
                        Map.of(
                                "example/Sting_Example.java",
                                "package example;\n  Sting_Example(\n      Object value)\n  {}\n")),
                "Sting_Example(\n    Object value)");
    }

    @Test
    public void failsWhenAnExampleDisappearsOrItsBoundaryChanges() {
        Assert.assertTrue(Assert.expectThrows(
                        IllegalArgumentException.class, () -> Markdown.snippet("file=missing.java", Map.of()))
                .getMessage()
                .contains("missing.java"));
        Assert.assertTrue(Assert.expectThrows(
                        IllegalArgumentException.class,
                        () -> Markdown.snippet(
                                "file=Example.java start_line=@Injectable",
                                Map.of("Example.java", "class Example {}\n")))
                .getMessage()
                .contains("@Injectable"));
    }

    @DataProvider
    public Object[][] headings() {
        return new Object[][] {
            {"@Typed on @Injectable types", "typed-on-injectable-types"},
            {"foo.bar(baz)?", "foobarbaz"},
            {"Version ...", "version-"},
            {"Café & Niño", "cafe-nino"}
        };
    }

    @Test(dataProvider = "headings")
    public void preservesPublicHeadingFragments(final String heading, final String fragment) {
        Assert.assertEquals(Markdown.slug(heading), fragment);
    }

    @Test
    public void fillsTemplatesOnceAndRejectsUnknownSlots() {
        Assert.assertEquals(
                SiteBuilder.template("<main>{{body}}</main>", Map.of("body", "{{title}} $1 \\")),
                "<main>{{title}} $1 \\</main>");
        Assert.expectThrows(IllegalArgumentException.class, () -> SiteBuilder.template("{{missing}}", Map.of()));
    }

    @Test
    public void readsTheExistingFrontmatterFormat() {
        final var page =
                SiteBuilder.readPage("inputs", "---\ntitle: Inputs\nsidebar_label: Injector Inputs\n---\nBody\n");
        Assert.assertEquals(page.title(), "Inputs");
        Assert.assertEquals(page.label(), "Injector Inputs");
        Assert.assertEquals(page.body(), "Body\n");
        Assert.expectThrows(
                IllegalArgumentException.class, () -> SiteBuilder.readPage("inputs", "---\nunknown: x\n---\n"));
    }

    @Test
    public void checksDecodedFragmentsDirectoryIndexesAndLocalAssets() throws IOException {
        final var root = site(Map.of(
                "index.html", "<a href=\"docs/#includes%28%29\">API</a><script src=\"local.js\"></script>",
                "docs/index.html", "<section id=\"includes()\"><a href=\"#includes()\">Method</a></section>",
                "local.js", "",
                "style.css", "body { background: url('icons/x.svg'); }",
                "icons/x.svg", "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>"));
        try {
            SiteLinks.check(root);
            Files.writeString(root.resolve("index.html"), "<a href=\"docs/#absent\">Broken</a>");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .getMessage()
                    .contains("#absent"));
            Files.writeString(root.resolve("index.html"), "<script src=\"https://example.com/code.js\"></script>");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .getMessage()
                    .contains("external asset"));
            Files.writeString(root.resolve("index.html"), "<a href=\"../outside.html\">Escapes</a>");
            Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            Files.writeString(root.resolve("index.html"), "<a href=\"https://example.com/\">External page</a>");
            Files.writeString(root.resolve("style.css"), "@import url('https://fonts.example.com/font.css');");
            Assert.expectThrows(IOException.class, () -> SiteLinks.check(root));
            Files.writeString(root.resolve("style.css"), "@import 'missing.css';");
            Assert.assertTrue(Assert.expectThrows(IOException.class, () -> SiteLinks.check(root))
                    .getMessage()
                    .contains("missing.css"));
            Files.writeString(
                    root.resolve("style.css"), "body { background: url('data:image/svg+xml; utf8, <svg></svg>'); }");
            SiteLinks.check(root);
        } finally {
            delete(root);
        }
    }

    @Test
    public void rejectsArchiveEntriesOutsideTheSite() {
        Assert.expectThrows(IOException.class, () -> SiteBuilder.destination(Path.of("site"), "../outside"));
    }

    private static Path site(final Map<String, String> files) throws IOException {
        final var root = Files.createTempDirectory("sting-site-test");
        for (final var entry : files.entrySet()) {
            final var file = root.resolve(entry.getKey());
            Files.createDirectories(file.getParent());
            Files.writeString(file, entry.getValue());
        }
        return root;
    }

    private static void delete(final Path root) throws IOException {
        try (var files = Files.walk(root)) {
            for (final var file : files.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(file);
            }
        }
    }
}
