package sting.integration;

import static org.testng.Assert.*;

import org.jspecify.annotations.NullMarked;
import org.testng.annotations.Test;
import sting.Factory;
import sting.Injectable;
import sting.Injector;

@NullMarked
public final class FactoryIntegrationTest extends AbstractIntegrationTest {
    @Injectable
    public static class SomeService extends BaseModel {}

    public static class MyComponent extends BaseModel {
        private final SomeService _someService;

        private final int _someParameter;

        MyComponent(final SomeService someService, final int someParameter) {
            super(someService, someParameter);
            _someService = someService;
            _someParameter = someParameter;
        }

        SomeService getSomeService() {
            return _someService;
        }

        int getSomeParameter() {
            return _someParameter;
        }
    }

    @Factory
    public interface MyComponentFactory {
        MyComponent create(int someParameter);
    }

    @Injector(fragmentOnly = false, includes = MyComponentFactory.class)
    public interface MyInjector {
        static MyInjector create() {
            return new FactoryIntegrationTest_Sting_MyInjector();
        }

        MyComponentFactory getFactory();
    }

    @Test
    public void scenario() {
        clearTrace();
        final var injector = MyInjector.create();

        assertCreateTrace("");

        final MyComponentFactory factory = injector.getFactory();
        assertNotNull(factory);

        assertCreateTrace("SomeService[]");

        final MyComponent component1 = factory.create(23);
        assertEquals(component1.getSomeParameter(), 23);

        assertCreateTrace("SomeService[] MyComponent[SomeService, 23]");

        final MyComponent component2 = factory.create(42);
        assertEquals(component2.getSomeParameter(), 42);
        assertNotSame(component1, component2);
        assertSame(component1.getSomeService(), component2.getSomeService());
        assertSame(factory, injector.getFactory());

        assertCreateTrace("SomeService[] MyComponent[SomeService, 23] MyComponent[SomeService, 42]");
    }
}
