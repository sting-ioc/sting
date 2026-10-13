package sting.doc.examples.naming;

import sting.Fragment;
import sting.Named;

@Fragment
public interface SimulationFragment {
    @Named("system:particleSystem")
    default SimulationSystem provideParticleSimulationSystem(@Named("system:lighting") SimulationSystem lighting) {
        // DOC ELIDE START
        throw new UnsupportedOperationException();
        // DOC ELIDE END
    }
    // DOC ELIDE START
    // DOC ELIDE END
}
