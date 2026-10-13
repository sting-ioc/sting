package sting.processor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Processor;
import org.realityforge.proton.qa.AbstractProcessorTest;

public abstract class AbstractStingProcessorTest extends AbstractProcessorTest {
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
