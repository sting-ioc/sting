package sting.processor;

import com.google.errorprone.BaseErrorProneJavaCompiler;
import com.google.errorprone.bugpatterns.ImpossibleNullComparison;
import com.google.errorprone.bugpatterns.UnusedVariable;
import com.google.errorprone.bugpatterns.nullness.UnnecessaryCheckNotNull;
import com.google.errorprone.scanner.ScannerSupplier;
import com.uber.nullaway.JSpecifyUnrecognizedAnnotationLocation;
import com.uber.nullaway.NullAway;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import org.realityforge.proton.qa.AbstractProcessorTest;

public abstract class AbstractStingProcessorTest extends AbstractProcessorTest {
    @Override
    protected JavaCompiler javaCompiler() {
        return Boolean.getBoolean("sting.plain_javac")
                ? super.javaCompiler()
                : new BaseErrorProneJavaCompiler(ScannerSupplier.fromBugCheckerClasses(
                        NullAway.class,
                        JSpecifyUnrecognizedAnnotationLocation.class,
                        UnusedVariable.class,
                        ImpossibleNullComparison.class,
                        UnnecessaryCheckNotNull.class));
    }

    @Override
    protected Processor processor() {
        return new StingProcessor();
    }

    @Override
    protected String getOptionPrefix() {
        return "sting";
    }

    @Override
    protected List<String> getOptions() {
        final List<String> options = new ArrayList<>(super.getOptions());
        options.add("-Asting.verbose_out_of_round.errors=false");
        options.add("-XDaddTypeAnnotationsToSymbol=true");
        if (!Boolean.getBoolean("sting.plain_javac")) {
            options.addAll(List.of(
                    "--release",
                    "17",
                    "-XDcompilePolicy=simple",
                    "-Xep:NullAway:ERROR",
                    "-Xep:JSpecifyUnrecognizedAnnotationLocation:ERROR",
                    "-Xep:UnusedVariable:ERROR",
                    "-Xep:ImpossibleNullComparison:ERROR",
                    "-Xep:UnnecessaryCheckNotNull:ERROR",
                    "-XepOpt:NullAway:OnlyNullMarked=true",
                    "-XepOpt:NullAway:JSpecifyMode=true"));
        }
        return options;
    }

    final String javaOutput(final String classname) {
        return toFilename(classname, "Sting_", ".java");
    }

    @SuppressWarnings("SameParameterValue")
    final String graphvizOutput(final String classname) {
        return toFilename(classname, "", StingProcessor.DOT_SUFFIX);
    }
}
