package sting.server.integration;

import static org.testng.Assert.*;

import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import sting.server.Transactional;

public final class ServerTransactionalIntegrationTest {
    @Nonnull
    private static final List<String> c_trace = new ArrayList<>();

    private static boolean c_startWithTransaction;

    @Transactional
    public interface DefaultService {
        void run();
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public interface RequiresNewService {
        void run();
    }

    @Injectable
    @Typed(DefaultService.class)
    public static class DefaultServiceImpl implements DefaultService {
        @Override
        public void run() {
            c_trace.add("target:default");
        }
    }

    @Injectable
    @Typed(RequiresNewService.class)
    public static class RequiresNewServiceImpl implements RequiresNewService {
        @Override
        public void run() {
            c_trace.add("target:requiresNew");
        }
    }

    @Injectable
    @Typed(TransactionManager.class)
    public static class TestTransactionManager implements TransactionManager {
        @Nullable
        private TestTransaction current;

        TestTransactionManager() {
            if (c_startWithTransaction) {
                current = new TestTransaction();
            }
        }

        @Override
        public void begin() throws NotSupportedException, SystemException {
            c_trace.add("tm:begin");
            current = new TestTransaction();
        }

        @Override
        public void commit()
                throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
            c_trace.add("tm:commit");
            current = null;
        }

        @Override
        public int getStatus() throws SystemException {
            c_trace.add("tm:getStatus");
            return null == current ? Status.STATUS_NO_TRANSACTION : current.status;
        }

        @Nullable
        @Override
        public Transaction getTransaction() throws SystemException {
            c_trace.add("tm:getTransaction");
            return current;
        }

        @Override
        public void resume(@Nonnull final Transaction transaction) throws InvalidTransactionException, SystemException {
            c_trace.add("tm:resume");
            current = (TestTransaction) transaction;
        }

        @Override
        public void rollback() throws SystemException {
            c_trace.add("tm:rollback");
            current = null;
        }

        @Override
        public void setRollbackOnly() throws SystemException {
            c_trace.add("tm:setRollbackOnly");
            if (null != current) {
                current.status = Status.STATUS_MARKED_ROLLBACK;
            }
        }

        @Override
        public void setTransactionTimeout(final int seconds) throws SystemException {}

        @Nullable
        @Override
        public Transaction suspend() throws SystemException {
            c_trace.add("tm:suspend");
            final TestTransaction transaction = current;
            current = null;
            return transaction;
        }
    }

    private static final class TestTransaction implements Transaction {
        private int status = Status.STATUS_ACTIVE;

        @Override
        public void commit() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean delistResource(@Nonnull final javax.transaction.xa.XAResource xaResource, final int flag) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean enlistResource(@Nonnull final javax.transaction.xa.XAResource xaResource) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getStatus() {
            return status;
        }

        @Override
        public void registerSynchronization(@Nonnull final jakarta.transaction.Synchronization synchronization) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void rollback() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRollbackOnly() {
            status = Status.STATUS_MARKED_ROLLBACK;
        }
    }

    @Injector(
            includes = {DefaultServiceImpl.class, TestTransactionManager.class},
            fragmentOnly = false)
    public interface DefaultInjector {
        @Nonnull
        static DefaultInjector create() {
            return new ServerTransactionalIntegrationTest_Sting_DefaultInjector();
        }

        @Nonnull
        DefaultService service();
    }

    @Injector(
            includes = {RequiresNewServiceImpl.class, TestTransactionManager.class},
            fragmentOnly = false)
    public interface RequiresNewInjector {
        @Nonnull
        static RequiresNewInjector create() {
            return new ServerTransactionalIntegrationTest_Sting_RequiresNewInjector();
        }

        @Nonnull
        RequiresNewService service();
    }

    @BeforeMethod
    public void reset() {
        c_trace.clear();
        c_startWithTransaction = false;
    }

    @Test
    public void defaultRequiredBindingSelectsRequiredInterceptor() {
        DefaultInjector.create().service().run();

        assertEquals(c_trace, List.of("tm:getTransaction", "tm:begin", "target:default", "tm:getStatus", "tm:commit"));
    }

    @Test
    public void nonDefaultRequiresNewBindingSelectsRequiresNewInterceptor() {
        c_startWithTransaction = true;

        RequiresNewInjector.create().service().run();

        assertEquals(
                c_trace,
                List.of(
                        "tm:getTransaction",
                        "tm:suspend",
                        "tm:begin",
                        "target:requiresNew",
                        "tm:getStatus",
                        "tm:commit",
                        "tm:resume"));
    }
}
