package sting.integration;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Eager;
import sting.Factory;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.Named;
import sting.Typed;
import sting.interceptors.After;
import sting.interceptors.AfterException;
import sting.interceptors.Arguments;
import sting.interceptors.Before;
import sting.interceptors.BindingValue;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.MethodName;
import sting.interceptors.Result;
import sting.interceptors.ServiceType;
import sting.interceptors.Thrown;
import static org.testng.Assert.*;

public final class InterceptorsIntegrationTest
{
  private static final List<String> c_trace = new ArrayList<>();
  private static boolean c_innerBeforeThrows;
  private static boolean c_innerAfterThrows;
  private static boolean c_innerAfterExceptionThrows;
  private static Supplier<SupplierCycleService> c_supplierCycleServiceSupplier;

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.TraceInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface Trace
  {
    String value() default "default";
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.OuterInterceptor", priority = 50 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface Outer
  {
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.InnerInterceptor", priority = 150 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface Inner
  {
  }

  @Trace( "service" )
  public interface MyService
  {
    @Nonnull
    String ok( @Nonnull String name );

    void fail()
      throws IOException;
  }

  @Outer
  @Inner
  public interface OrderedService
  {
    void run();

    void fail();
  }

  public interface ConstructorConsumer
  {
    @Nonnull
    MyService service();
  }

  public interface ProviderConsumer
  {
    @Nonnull
    MyService service();
  }

  public interface ImplBoundService
  {
    void ping();
  }

  public interface ProviderBoundService
  {
    void ping();
  }

  public interface MultiA
  {
    void callA();
  }

  public interface MultiB
  {
    void callB();
  }

  public interface QualifiedService
  {
    @Nonnull
    String name();
  }

  public interface OverrideService
  {
    void run();
  }

  public interface ArgumentService
  {
    void run( @Nonnull String value );
  }

  public interface AsyncService
  {
    @Nonnull
    AsyncHandle start();
  }

  public interface SupplierCycleService
  {
    void run();
  }

  public interface EagerService
  {
    void run();
  }

  @Trace( "factory" )
  @Factory
  public interface GeneratedFactory
  {
    @Nonnull
    FactoryProduct create( @Nonnull String name );
  }

  public static final class FactoryProduct
  {
    @Nonnull
    private final String _name;

    FactoryProduct( @Nonnull final String name )
    {
      _name = name;
      c_trace.add( "target:factory:" + name );
    }

    @Nonnull
    @Override
    public String toString()
    {
      return _name;
    }
  }

  public static final class AsyncHandle
  {
    void complete()
    {
      c_trace.add( "handle:complete" );
    }
  }

  @Injectable
  @Typed( MyService.class )
  public static class MyServiceImpl
    implements MyService
  {
    @Nonnull
    @Override
    public String ok( @Nonnull final String name )
    {
      c_trace.add( "target:" + name );
      return "ok:" + name;
    }

    @Override
    public void fail()
      throws IOException
    {
      c_trace.add( "target:fail" );
      throw new IOException( "boom" );
    }
  }

  @Injectable
  @Typed( OrderedService.class )
  public static class OrderedServiceImpl
    implements OrderedService
  {
    @Override
    public void run()
    {
      c_trace.add( "target:ordered" );
    }

    @Override
    public void fail()
    {
      c_trace.add( "target:orderedFail" );
      throw new IllegalStateException( "ordered" );
    }
  }

  @Injectable
  @Typed( ConstructorConsumer.class )
  public static class ConstructorConsumerImpl
    implements ConstructorConsumer
  {
    @Nonnull
    private final MyService _service;

    ConstructorConsumerImpl( @Nonnull final MyService service )
    {
      _service = service;
    }

    @Nonnull
    @Override
    public MyService service()
    {
      return _service;
    }
  }

  @Trace( "implementation" )
  @Injectable
  @Typed( ImplBoundService.class )
  public static class ImplBoundServiceImpl
    implements ImplBoundService
  {
    @Override
    public void ping()
    {
      c_trace.add( "target:implBound" );
    }
  }

  public static class ProviderBoundServiceImpl
    implements ProviderBoundService
  {
    @Override
    public void ping()
    {
      c_trace.add( "target:providerBound" );
    }
  }

  @Trace( "multi" )
  @Injectable
  @Typed( { MultiA.class, MultiB.class } )
  public static class MultiServiceImpl
    implements MultiA, MultiB
  {
    @Override
    public void callA()
    {
      c_trace.add( "target:multiA" );
    }

    @Override
    public void callB()
    {
      c_trace.add( "target:multiB" );
    }
  }

  @Trace( "left" )
  @Named( "left" )
  @Injectable
  @Typed( QualifiedService.class )
  public static class LeftQualifiedService
    implements QualifiedService
  {
    @Nonnull
    @Override
    public String name()
    {
      return "left";
    }
  }

  @Trace( "right" )
  @Named( "right" )
  @Injectable
  @Typed( QualifiedService.class )
  public static class RightQualifiedService
    implements QualifiedService
  {
    @Nonnull
    @Override
    public String name()
    {
      return "right";
    }
  }

  @OverrideTrace
  @Injectable
  @Typed( OverrideService.class )
  public static class OverrideServiceImpl
    implements OverrideService
  {
    @Override
    public void run()
    {
      c_trace.add( "target:override" );
    }
  }

  @MutateArguments
  @Injectable
  @Typed( ArgumentService.class )
  public static class ArgumentServiceImpl
    implements ArgumentService
  {
    @Override
    public void run( @Nonnull final String value )
    {
      c_trace.add( "target:argument:" + value );
    }
  }

  @Trace( "async" )
  @Injectable
  @Typed( AsyncService.class )
  public static class AsyncServiceImpl
    implements AsyncService
  {
    @Nonnull
    @Override
    public AsyncHandle start()
    {
      c_trace.add( "target:async" );
      return new AsyncHandle();
    }
  }

  @SupplierCycleTrace
  @Injectable
  @Typed( SupplierCycleService.class )
  public static class SupplierCycleServiceImpl
    implements SupplierCycleService
  {
    @Override
    public void run()
    {
      c_trace.add( "target:supplierCycle" );
    }
  }

  @EagerTrace
  @Eager
  @Injectable
  @Typed( EagerService.class )
  public static class EagerServiceImpl
    implements EagerService
  {
    EagerServiceImpl()
    {
      c_trace.add( "target:eager:create" );
    }

    @Override
    public void run()
    {
      c_trace.add( "target:eager" );
    }
  }

  @Fragment
  public interface MyFragment
  {
    @Trace( "provider" )
    default ProviderBoundService providerBoundService()
    {
      return new ProviderBoundServiceImpl();
    }

    default ProviderConsumer providerConsumer( @Nonnull final MyService service )
    {
      return () -> service;
    }
  }

  @Injectable
  public static class TraceInterceptor
  {
    TraceInterceptor()
    {
    }

    @Before
    public void before( @ServiceType @Nonnull final String serviceType,
                        @MethodName @Nonnull final String methodName,
                        @BindingValue( "value" ) @Nonnull final String value,
                        @Arguments @Nonnull final Object[] arguments )
    {
      c_trace.add( "before:" + serviceType.substring( serviceType.lastIndexOf( '.' ) + 1 ) + "." + methodName +
                   ":" + value + ":" + arguments.length );
    }

    @After
    public void after( @MethodName @Nonnull final String methodName, @Result final Object result )
    {
      c_trace.add( "after:" + methodName + ":" + result );
    }

    @AfterException
    public void afterException( @MethodName @Nonnull final String methodName,
                                @Thrown @Nonnull final Throwable throwable )
    {
      c_trace.add( "exception:" + methodName + ":" + throwable.getClass().getSimpleName() );
    }
  }

  @Injectable
  public static class OuterInterceptor
  {
    OuterInterceptor()
    {
    }

    @Before
    public void before()
    {
      c_trace.add( "outer:before" );
    }

    @After
    public void after()
    {
      c_trace.add( "outer:after" );
    }

    @AfterException
    public void afterException( @Thrown @Nonnull final Throwable throwable )
    {
      c_trace.add( "outer:exception:" + throwable.getMessage() );
    }
  }

  @Injectable
  public static class InnerInterceptor
  {
    InnerInterceptor()
    {
    }

    @Before
    public void before()
    {
      c_trace.add( "inner:before" );
      if ( c_innerBeforeThrows )
      {
        throw new IllegalStateException( "inner-before" );
      }
    }

    @After
    public void after()
    {
      c_trace.add( "inner:after" );
      if ( c_innerAfterThrows )
      {
        throw new IllegalStateException( "inner-after" );
      }
    }

    @AfterException
    public void afterException( @Thrown @Nonnull final Throwable throwable )
    {
      c_trace.add( "inner:exception:" + throwable.getMessage() );
      if ( c_innerAfterExceptionThrows )
      {
        throw new IllegalArgumentException( "inner-afterException" );
      }
    }
  }

  public static class BaseOverrideInterceptor
  {
    @Before
    public void before()
    {
      c_trace.add( "base:override:before" );
    }
  }

  @Injectable
  public static class OverrideInterceptor
    extends BaseOverrideInterceptor
  {
    OverrideInterceptor()
    {
    }

    @Before
    public void before()
    {
      c_trace.add( "override:before" );
    }
  }

  @Injectable
  public static class MutatingArgumentsInterceptor
  {
    MutatingArgumentsInterceptor()
    {
    }

    @Before
    public void before( @Arguments @Nonnull final Object[] arguments )
    {
      arguments[ 0 ] = "changed";
      c_trace.add( "mutated:argument" );
    }
  }

  @Injectable
  public static class SupplierCycleInterceptor
  {
    SupplierCycleInterceptor( @Nonnull final Supplier<SupplierCycleService> service )
    {
      c_supplierCycleServiceSupplier = service;
    }

    @Before
    public void before()
    {
      c_trace.add( "supplierCycle:before" );
    }
  }

  @Injectable
  public static class EagerInterceptor
  {
    EagerInterceptor()
    {
      c_trace.add( "interceptor:eager:create" );
    }

    @Before
    public void before()
    {
      c_trace.add( "eager:before" );
    }
  }

  @Injector( includes = { MyServiceImpl.class,
                          OrderedServiceImpl.class,
                          ConstructorConsumerImpl.class,
                          ImplBoundServiceImpl.class,
                          MultiServiceImpl.class,
                          LeftQualifiedService.class,
                          RightQualifiedService.class,
                          OverrideServiceImpl.class,
                          ArgumentServiceImpl.class,
                          AsyncServiceImpl.class,
                          GeneratedFactory.class,
                          MyFragment.class }, fragmentOnly = false )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new InterceptorsIntegrationTest_Sting_MyInjector();
    }

    @Nonnull
    MyService service();

    @Nonnull
    Optional<MyService> optionalService();

    @Nonnull
    Supplier<MyService> serviceSupplier();

    @Nonnull
    Supplier<Optional<MyService>> optionalServiceSupplier();

    @Nonnull
    Collection<MyService> serviceCollection();

    @Nonnull
    Collection<Supplier<MyService>> serviceSupplierCollection();

    @Nonnull
    Collection<Supplier<Optional<MyService>>> optionalServiceSupplierCollection();

    @Nonnull
    OrderedService orderedService();

    @Nonnull
    ConstructorConsumer constructorConsumer();

    @Nonnull
    ProviderConsumer providerConsumer();

    @Nonnull
    ImplBoundService implBoundService();

    @Nonnull
    ProviderBoundService providerBoundService();

    @Nonnull
    MultiA multiA();

    @Nonnull
    MultiB multiB();

    @Named( "left" )
    @Nonnull
    QualifiedService leftService();

    @Named( "right" )
    @Nonnull
    QualifiedService rightService();

    @Nonnull
    OverrideService overrideService();

    @Nonnull
    ArgumentService argumentService();

    @Nonnull
    AsyncService asyncService();

    @Nonnull
    GeneratedFactory generatedFactory();
  }

  @Injector( includes = SupplierCycleServiceImpl.class, fragmentOnly = false )
  public interface SupplierCycleInjector
  {
    @Nonnull
    static SupplierCycleInjector create()
    {
      return new InterceptorsIntegrationTest_Sting_SupplierCycleInjector();
    }

    @Nonnull
    SupplierCycleService service();
  }

  @Injector( includes = EagerServiceImpl.class, fragmentOnly = false )
  public interface EagerInjector
  {
    @Nonnull
    static EagerInjector create()
    {
      return new InterceptorsIntegrationTest_Sting_EagerInjector();
    }

    @Nonnull
    EagerService service();
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.OverrideInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface OverrideTrace
  {
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.MutatingArgumentsInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface MutateArguments
  {
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.SupplierCycleInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface SupplierCycleTrace
  {
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.EagerInterceptor", priority = 100 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface EagerTrace
  {
  }

  @Test
  public void lifecycleAroundSuccessfulCall()
  {
    c_trace.clear();
    final MyService service = MyInjector.create().service();

    assertEquals( service.ok( "a" ), "ok:a" );
    assertEquals( c_trace,
                  List.of( "before:MyService.ok:service:1",
                           "target:a",
                           "after:ok:ok:a" ) );
  }

  @Test
  public void lifecycleAroundCheckedException()
  {
    c_trace.clear();
    final MyService service = MyInjector.create().service();

    final IOException exception = expectThrows( IOException.class, service::fail );
    assertEquals( exception.getMessage(), "boom" );
    assertEquals( c_trace,
                  List.of( "before:MyService.fail:service:0",
                           "target:fail",
                           "exception:fail:IOException" ) );
  }

  @Test
  public void priorityOrdering()
  {
    c_innerBeforeThrows = false;
    c_innerAfterThrows = false;
    c_trace.clear();
    MyInjector.create().orderedService().run();

    assertEquals( c_trace,
                  List.of( "outer:before",
                           "inner:before",
                           "target:ordered",
                           "inner:after",
                           "outer:after" ) );
  }

  @Test
  public void requestKindsReturnCachedProxy()
  {
    final MyInjector injector = MyInjector.create();
    final MyService service = injector.service();

    assertSame( injector.optionalService().orElseThrow(), service );
    assertSame( injector.serviceSupplier().get(), service );
    assertSame( injector.optionalServiceSupplier().get().orElseThrow(), service );
    assertEquals( injector.serviceCollection().size(), 1 );
    assertSame( injector.serviceCollection().iterator().next(), service );
    assertEquals( injector.serviceSupplierCollection().size(), 1 );
    assertSame( injector.serviceSupplierCollection().iterator().next().get(), service );
    assertEquals( injector.optionalServiceSupplierCollection().size(), 1 );
    assertSame( injector.optionalServiceSupplierCollection().iterator().next().get().orElseThrow(), service );
  }

  @Test
  public void outputRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertSame( injector.service(), injector.service() );
  }

  @Test
  public void optionalRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertSame( injector.optionalService().orElseThrow(), injector.service() );
  }

  @Test
  public void supplierRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertSame( injector.serviceSupplier().get(), injector.service() );
  }

  @Test
  public void supplierOptionalRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertSame( injector.optionalServiceSupplier().get().orElseThrow(), injector.service() );
  }

  @Test
  public void collectionRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertEquals( injector.serviceCollection().size(), 1 );
    assertSame( injector.serviceCollection().iterator().next(), injector.service() );
  }

  @Test
  public void supplierCollectionRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertEquals( injector.serviceSupplierCollection().size(), 1 );
    assertSame( injector.serviceSupplierCollection().iterator().next().get(), injector.service() );
  }

  @Test
  public void supplierOptionalCollectionRequestReturnsCachedProxy()
  {
    final MyInjector injector = MyInjector.create();

    assertEquals( injector.optionalServiceSupplierCollection().size(), 1 );
    assertSame( injector.optionalServiceSupplierCollection().iterator().next().get().orElseThrow(),
                injector.service() );
  }

  @Test
  public void dependenciesReceiveCachedProxy()
  {
    final MyInjector injector = MyInjector.create();
    final MyService service = injector.service();

    assertSame( injector.constructorConsumer().service(), service );
    assertSame( injector.providerConsumer().service(), service );
  }

  @Test
  public void bindingSourceAnnotationsAreApplied()
  {
    c_trace.clear();
    MyInjector.create().implBoundService().ping();
    assertEquals( c_trace,
                  List.of( "before:ImplBoundService.ping:implementation:0",
                           "target:implBound",
                           "after:ping:null" ) );

    c_trace.clear();
    MyInjector.create().providerBoundService().ping();
    assertEquals( c_trace,
                  List.of( "before:ProviderBoundService.ping:provider:0",
                           "target:providerBound",
                           "after:ping:null" ) );
  }

  @Test
  public void multiplePublishedServicesReceiveDifferentProxies()
  {
    final MyInjector injector = MyInjector.create();
    final MultiA multiA = injector.multiA();
    final MultiB multiB = injector.multiB();

    assertNotSame( multiA, multiB );

    c_trace.clear();
    multiA.callA();
    multiB.callB();
    assertEquals( c_trace,
                  List.of( "before:MultiA.callA:multi:0",
                           "target:multiA",
                           "after:callA:null",
                           "before:MultiB.callB:multi:0",
                           "target:multiB",
                           "after:callB:null" ) );
  }

  @Test
  public void qualifiedServicesReceiveDistinctProxies()
  {
    final MyInjector injector = MyInjector.create();

    assertEquals( injector.leftService().name(), "left" );
    assertEquals( injector.rightService().name(), "right" );
    assertNotSame( injector.leftService(), injector.rightService() );
  }

  @Test
  public void afterExceptionOrderingForRuntimeFailure()
  {
    c_innerBeforeThrows = false;
    c_innerAfterThrows = false;
    c_trace.clear();

    final IllegalStateException exception = expectThrows( IllegalStateException.class,
                                                          () -> MyInjector.create().orderedService().fail() );
    assertEquals( exception.getMessage(), "ordered" );
    assertEquals( c_trace,
                  List.of( "outer:before",
                           "inner:before",
                           "target:orderedFail",
                           "inner:exception:ordered",
                           "outer:exception:ordered" ) );
  }

  @Test
  public void replacementAfterExceptionFailureIsObservedByOuterInterceptor()
  {
    c_innerBeforeThrows = false;
    c_innerAfterThrows = false;
    c_innerAfterExceptionThrows = true;
    c_trace.clear();
    try
    {
      final IllegalArgumentException exception = expectThrows( IllegalArgumentException.class,
                                                               () -> MyInjector.create().orderedService().fail() );
      assertEquals( exception.getMessage(), "inner-afterException" );
      assertEquals( c_trace,
                    List.of( "outer:before",
                             "inner:before",
                             "target:orderedFail",
                             "inner:exception:ordered",
                             "outer:exception:inner-afterException" ) );
    }
    finally
    {
      c_innerAfterExceptionThrows = false;
    }
  }

  @Test
  public void ownBeforeFailureIsOnlyObservedByOuterInterceptor()
  {
    c_innerBeforeThrows = true;
    c_innerAfterThrows = false;
    c_trace.clear();
    try
    {
      final IllegalStateException exception = expectThrows( IllegalStateException.class,
                                                            () -> MyInjector.create().orderedService().run() );
      assertEquals( exception.getMessage(), "inner-before" );
      assertEquals( c_trace,
                    List.of( "outer:before",
                             "inner:before",
                             "outer:exception:inner-before" ) );
    }
    finally
    {
      c_innerBeforeThrows = false;
    }
  }

  @Test
  public void ownAfterFailureIsOnlyObservedByOuterInterceptor()
  {
    c_innerBeforeThrows = false;
    c_innerAfterThrows = true;
    c_trace.clear();
    try
    {
      final IllegalStateException exception = expectThrows( IllegalStateException.class,
                                                            () -> MyInjector.create().orderedService().run() );
      assertEquals( exception.getMessage(), "inner-after" );
      assertEquals( c_trace,
                    List.of( "outer:before",
                             "inner:before",
                             "target:ordered",
                             "inner:after",
                             "outer:exception:inner-after" ) );
    }
    finally
    {
      c_innerAfterThrows = false;
    }
  }

  @Test
  public void subclassOverrideRedeclaringLifecycleAnnotationRuns()
  {
    c_trace.clear();

    MyInjector.create().overrideService().run();

    assertEquals( c_trace,
                  List.of( "override:before",
                           "target:override" ) );
  }

  @Test
  public void factoryInterfaceIsInterceptedAtSynchronousCallBoundary()
  {
    c_trace.clear();

    final FactoryProduct product = MyInjector.create().generatedFactory().create( "made" );

    assertEquals( product.toString(), "made" );
    assertEquals( c_trace,
                  List.of( "before:GeneratedFactory.create:factory:1",
                           "target:factory:made",
                           "after:create:made" ) );
  }

  @Test
  public void eagerInterceptionCreatesTargetAndInterceptorWithInjector()
  {
    c_trace.clear();

    final EagerInjector injector = EagerInjector.create();

    assertEquals( c_trace, List.of( "interceptor:eager:create", "target:eager:create" ) );

    c_trace.clear();
    injector.service().run();
    assertEquals( c_trace, List.of( "eager:before", "target:eager" ) );
  }

  @Test
  public void argumentsMutationDoesNotRewriteTargetCall()
  {
    c_trace.clear();

    MyInjector.create().argumentService().run( "original" );

    assertEquals( c_trace,
                  List.of( "mutated:argument",
                           "target:argument:original" ) );
  }

  @Test
  public void asyncShapedReturnIsInterceptedAtSynchronousBoundaryOnly()
  {
    c_trace.clear();

    final AsyncHandle handle = MyInjector.create().asyncService().start();

    assertEquals( List.of( "before:AsyncService.start:async:0",
                           "target:async",
                           "after:start:" + handle ),
                  c_trace );

    handle.complete();
    assertEquals( List.of( "before:AsyncService.start:async:0",
                           "target:async",
                           "after:start:" + handle,
                           "handle:complete" ),
                  c_trace );
  }

  @Test
  public void supplierBoundaryCycleReceivesProxyWhenInvoked()
  {
    c_trace.clear();
    c_supplierCycleServiceSupplier = null;

    final SupplierCycleService service = SupplierCycleInjector.create().service();
    assertNotNull( c_supplierCycleServiceSupplier );
    assertSame( c_supplierCycleServiceSupplier.get(), service );

    service.run();
    assertEquals( c_trace,
                  List.of( "supplierCycle:before",
                           "target:supplierCycle" ) );
  }
}
