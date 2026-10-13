package sting.integration;

import static org.testng.Assert.*;

import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import sting.Injectable;
import sting.Injector;
import sting.StingProvider;
import sting.Typed;

@NullMarked
public final class ProviderInjectableAutoDiscoveryIntegrationTest extends AbstractIntegrationTest {
    @Injectable
    public static class Model2 extends BaseModel {}

    @StingProvider("[CompoundName]Impl")
    @interface MyFrameworkComponent {}

    @MyFrameworkComponent
    public static class Model1 extends BaseModel {
        private final Model2 _model2;

        Model1(final Model2 model2) {
            super(model2);
            _model2 = model2;
        }

        Model2 getModel2() {
            return _model2;
        }
    }

    @Injectable
    @Typed(Model1.class)
    public static class Model1Impl extends Model1 {
        Model1Impl(final Model2 model2) {
            super(model2);
        }
    }

    @Injector
    public interface MyInjector {
        static MyInjector create() {
            return new ProviderInjectableAutoDiscoveryIntegrationTest_Sting_MyInjector();
        }

        Model1 getModel1();

        Model2 getModel2();
    }

    @Test
    public void scenario() {
        clearTrace();
        final var injector = MyInjector.create();

        assertCreateTrace("");

        final Model1 model1 = injector.getModel1();
        assertNotNull(model1);

        assertCreateTrace("Model2[] Model1Impl[Model2]");

        final Model2 model2 = injector.getModel2();
        assertNotNull(model2);
        assertSame(model2, model1.getModel2());

        assertCreateTrace("Model2[] Model1Impl[Model2]");
    }
}
