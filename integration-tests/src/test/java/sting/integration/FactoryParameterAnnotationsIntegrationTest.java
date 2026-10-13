package sting.integration;

import static org.testng.Assert.*;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.testng.annotations.Test;
import sting.Factory;
import sting.Injectable;
import sting.Injector;

@NullMarked
public final class FactoryParameterAnnotationsIntegrationTest extends AbstractIntegrationTest {
    private static final class StableRunnable implements Runnable {
        @Override
        public void run() {}

        @Override
        public String toString() {
            return "StableRunnable";
        }
    }

    @Injectable
    public static class SomeService extends BaseModel {}

    public static class MyComponent extends BaseModel {
        private final SomeService _someService;

        @Nullable
        private final String _name;

        private final Runnable _action;

        private final int _count;

        MyComponent(
                final SomeService someService, @Nullable final String name, final Runnable action, final int count) {
            super(someService, name, action, count);
            _someService = someService;
            _name = name;
            _action = action;
            _count = count;
        }

        SomeService getSomeService() {
            return _someService;
        }

        @Nullable
        String getName() {
            return _name;
        }

        Runnable getAction() {
            return _action;
        }

        int getCount() {
            return _count;
        }
    }

    @Factory
    public interface MyComponentFactory {
        MyComponent create(@Nullable String name, Runnable action, int count);
    }

    @Injector(fragmentOnly = false, includes = MyComponentFactory.class)
    public interface MyInjector {
        static MyInjector create() {
            return new FactoryParameterAnnotationsIntegrationTest_Sting_MyInjector();
        }

        MyComponentFactory getFactory();
    }

    @Test
    public void scenario() {
        clearTrace();
        final var injector = MyInjector.create();
        final MyComponentFactory factory = injector.getFactory();
        final Runnable action = new StableRunnable();

        final MyComponent component1 = factory.create(null, action, 17);
        assertNull(component1.getName());
        assertSame(action, component1.getAction());
        assertEquals(component1.getCount(), 17);

        final MyComponent component2 = factory.create("beta", action, 23);
        assertEquals(component2.getName(), "beta");
        assertSame(action, component2.getAction());
        assertEquals(component2.getCount(), 23);
        assertSame(component1.getSomeService(), component2.getSomeService());

        assertCreateTrace("SomeService[] MyComponent[SomeService, null, StableRunnable, 17] "
                + "MyComponent[SomeService, beta, StableRunnable, 23]");
    }
}
