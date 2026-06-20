package sting.integration;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import sting.interceptors.Around;
import sting.interceptors.Before;
import sting.interceptors.BindingValue;
import sting.interceptors.InterceptorBinding;
import sting.interceptors.MethodName;
import sting.interceptors.Proceed;
import sting.interceptors.Invocation;
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
  private static AroundMode c_outerAroundMode = AroundMode.PROCEED;
  private static AroundMode c_innerAroundMode = AroundMode.PROCEED;

  private enum AroundMode
  {
    PROCEED,
    SHORT_CIRCUIT,
    THROW_BEFORE_PROCEED,
    THROW_AFTER_PROCEED,
    DOUBLE_PROCEED,
    REPLACE_ARGUMENTS,
    NULL_REPLACEMENT,
    WRONG_COUNT_REPLACEMENT,
    WRONG_COUNT_THEN_PROCEED,
    VARARGS_REPLACE,
    VARARGS_WRONG_COUNT,
    WRONG_TYPE_REPLACEMENT,
    NULL_PRIMITIVE_REPLACEMENT,
    VOID_SHORT_CIRCUIT_VALUE,
    RETURN_NULL,
    RETURN_WRONG_WRAPPER,
    THROW_DECLARED,
    THROW_UNDECLARED,
    THROW_RUNTIME,
    THROW_ERROR
  }

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

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.OuterAroundInterceptor", priority = 40 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface OuterAround
  {
    String value() default "outer";
  }

  @InterceptorBinding( implementedBy = "sting.integration.InterceptorsIntegrationTest.InnerAroundInterceptor", priority = 140 )
  @Retention( RetentionPolicy.CLASS )
  @Target( { ElementType.TYPE, ElementType.METHOD } )
  public @interface InnerAround
  {
    String value() default "inner";
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

  @OuterAround
  @InnerAround
  public interface AroundService
  {
    @Nonnull
    String echo( @Nonnull String value );

    @Nonnull
    String replace( @Nonnull String value, int count );

    @Nonnull
    String varargs( @Nonnull String prefix, @Nonnull String... values );

    int primitive( @Nonnull String value );

    int primitiveArg( int value );

    void none( @Nonnull String value );

    @Nonnull
    String checked()
      throws IOException;

    @Nonnull
    String runtime();
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
  @Typed( AroundService.class )
  public static class AroundServiceImpl
    implements AroundService
  {
    @Nonnull
    @Override
    public String echo( @Nonnull final String value )
    {
      c_trace.add( "target:echo:" + value );
      return "target:" + value;
    }

    @Nonnull
    @Override
    public String replace( @Nonnull final String value, final int count )
    {
      c_trace.add( "target:replace:" + value + ":" + count );
      return value + ":" + count;
    }

    @Nonnull
    @Override
    public String varargs( @Nonnull final String prefix, @Nonnull final String... values )
    {
      c_trace.add( "target:varargs:" + prefix + ":" + Arrays.toString( values ) );
      return prefix + ":" + String.join( ",", values );
    }

    @Override
    public int primitive( @Nonnull final String value )
    {
      c_trace.add( "target:primitive:" + value );
      return value.length();
    }

    @Override
    public int primitiveArg( final int value )
    {
      c_trace.add( "target:primitiveArg:" + value );
      return value + 1;
    }

    @Override
    public void none( @Nonnull final String value )
    {
      c_trace.add( "target:none:" + value );
    }

    @SuppressWarnings( "RedundantThrows" )
    @Nonnull
    @Override
    public String checked()
      throws IOException
    {
      c_trace.add( "target:checked" );
      return "checked";
    }

    @Nonnull
    @Override
    public String runtime()
    {
      c_trace.add( "target:runtime" );
      throw new IllegalStateException( "target-runtime" );
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

  @Injectable
  public static class OuterAroundInterceptor
  {
    OuterAroundInterceptor()
    {
    }

    @Before
    public void before()
    {
      c_trace.add( "outerAround:before" );
    }

    @Around
    public Object around( @Proceed @Nonnull final Invocation invocation,
                          @ServiceType @Nonnull final String serviceType,
                          @MethodName @Nonnull final String methodName,
                          @BindingValue( "value" ) @Nonnull final String binding,
                          @Arguments @Nonnull final Object[] arguments )
      throws Throwable
    {
      return InterceptorsIntegrationTest.around( "outerAround",
                                                c_outerAroundMode,
                                                invocation,
                                                serviceType,
                                                methodName,
                                                binding,
                                                arguments );
    }

    @After
    public void after( @Result final Object result )
    {
      c_trace.add( "outerAround:after:" + result );
    }

    @AfterException
    public void afterException( @Thrown @Nonnull final Throwable throwable )
    {
      c_trace.add( "outerAround:exception:" + describe( throwable ) );
    }
  }

  @Injectable
  public static class InnerAroundInterceptor
  {
    InnerAroundInterceptor()
    {
    }

    @Before
    public void before()
    {
      c_trace.add( "innerAround:before" );
    }

    @Around
    public Object around( @Proceed @Nonnull final Invocation invocation,
                          @ServiceType @Nonnull final String serviceType,
                          @MethodName @Nonnull final String methodName,
                          @BindingValue( "value" ) @Nonnull final String binding,
                          @Arguments @Nonnull final Object[] arguments )
      throws Throwable
    {
      return InterceptorsIntegrationTest.around( "innerAround",
                                                c_innerAroundMode,
                                                invocation,
                                                serviceType,
                                                methodName,
                                                binding,
                                                arguments );
    }

    @After
    public void after( @Result final Object result )
    {
      c_trace.add( "innerAround:after:" + result );
    }

    @AfterException
    public void afterException( @Thrown @Nonnull final Throwable throwable )
    {
      c_trace.add( "innerAround:exception:" + describe( throwable ) );
    }
  }

  @Nullable
  private static Object around( @Nonnull final String name,
                                @Nonnull final AroundMode mode,
                                @Nonnull final Invocation invocation,
                                @Nonnull final String serviceType,
                                @Nonnull final String methodName,
                                @Nonnull final String binding,
                                @Nonnull final Object[] arguments )
    throws Throwable
  {
    c_trace.add( name + ":around:enter:" + serviceType.substring( serviceType.lastIndexOf( '.' ) + 1 ) + "." +
                 methodName + ":" + binding + ":" + Arrays.toString( arguments ) );
    return switch ( mode )
    {
      case PROCEED -> proceed( name, invocation );
      case SHORT_CIRCUIT -> "short:" + methodName;
      case THROW_BEFORE_PROCEED -> throw new IllegalStateException( name + "-before" );
      case THROW_AFTER_PROCEED ->
      {
        final Object result = invocation.proceed();
        c_trace.add( name + ":around:afterProceed:" + result );
        throw new IllegalStateException( name + "-after" );
      }
      case DOUBLE_PROCEED ->
      {
        final Object result = invocation.proceed();
        c_trace.add( name + ":around:firstProceed:" + result );
        yield invocation.proceed();
      }
      case REPLACE_ARGUMENTS -> proceed( name, invocation, new Object[]{ "replacement", 2 } );
      case NULL_REPLACEMENT -> invocation.proceed( null );
      case WRONG_COUNT_REPLACEMENT -> invocation.proceed( new Object[]{ "only" } );
      case WRONG_COUNT_THEN_PROCEED ->
      {
        try
        {
          invocation.proceed( new Object[]{ "only" } );
        }
        catch ( final AssertionError e )
        {
          c_trace.add( name + ":wrong-count" );
        }
        yield proceed( name, invocation, new Object[]{ "fixed", 3 } );
      }
      case VARARGS_REPLACE -> proceed( name, invocation, new Object[]{ "changed", new String[]{ "x", "y" } } );
      case VARARGS_WRONG_COUNT -> invocation.proceed( new Object[]{ "changed", "x", "y" } );
      case WRONG_TYPE_REPLACEMENT -> invocation.proceed( new Object[]{ Integer.valueOf( 1 ), 2 } );
      case NULL_PRIMITIVE_REPLACEMENT -> invocation.proceed( new Object[]{ null } );
      case VOID_SHORT_CIRCUIT_VALUE -> "ignored";
      case RETURN_NULL -> null;
      case RETURN_WRONG_WRAPPER -> "wrong";
      case THROW_DECLARED -> throw new IOException( name + "-declared" );
      case THROW_UNDECLARED -> throw new Exception( name + "-undeclared" );
      case THROW_RUNTIME -> throw new IllegalStateException( name + "-runtime" );
      case THROW_ERROR -> throw new AssertionError( name + "-error" );
    };
  }

  private static Object proceed( @Nonnull final String name, @Nonnull final Invocation invocation )
    throws Throwable
  {
    try
    {
      final Object result = invocation.proceed();
      c_trace.add( name + ":around:exit:" + result );
      return result;
    }
    catch ( final Throwable t )
    {
      c_trace.add( name + ":around:catch:" + describe( t ) );
      throw t;
    }
  }

  private static Object proceed( @Nonnull final String name,
                                 @Nonnull final Invocation invocation,
                                 @Nonnull final Object[] arguments )
    throws Throwable
  {
    try
    {
      final Object result = invocation.proceed( arguments );
      c_trace.add( name + ":around:exit:" + result );
      return result;
    }
    catch ( final Throwable t )
    {
      c_trace.add( name + ":around:catch:" + describe( t ) );
      throw t;
    }
  }

  @Nonnull
  private static String describe( @Nonnull final Throwable throwable )
  {
    return throwable.getClass().getSimpleName() + ":" + throwable.getMessage();
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
    AsyncService asyncService();

    @Nonnull
    GeneratedFactory generatedFactory();
  }

  @Injector( includes = AroundServiceImpl.class, fragmentOnly = false )
  public interface AroundInjector
  {
    @Nonnull
    static AroundInjector create()
    {
      return new InterceptorsIntegrationTest_Sting_AroundInjector();
    }

    @Nonnull
    AroundService service();
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

  private void resetAroundState()
  {
    c_trace.clear();
    c_outerAroundMode = AroundMode.PROCEED;
    c_innerAroundMode = AroundMode.PROCEED;
  }

  @Test
  public void aroundPriorityAndLifecycleOrdering()
  {
    resetAroundState();

    assertEquals( AroundInjector.create().service().echo( "a" ), "target:a" );

    assertEquals( c_trace,
                  List.of( "outerAround:before",
                           "outerAround:around:enter:AroundService.echo:outer:[a]",
                           "innerAround:before",
                           "innerAround:around:enter:AroundService.echo:inner:[a]",
                           "target:echo:a",
                           "innerAround:around:exit:target:a",
                           "innerAround:after:target:a",
                           "outerAround:around:exit:target:a",
                           "outerAround:after:target:a" ) );
  }

  @Test
  public void aroundShortCircuitIsSuccessfulInvocation()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.SHORT_CIRCUIT;

    assertEquals( AroundInjector.create().service().echo( "a" ), "short:echo" );

    assertEquals( c_trace,
                  List.of( "outerAround:before",
                           "outerAround:around:enter:AroundService.echo:outer:[a]",
                           "outerAround:after:short:echo" ) );
  }

  @Test
  public void ownAroundFailureBeforeProceedIsObservedByOwnAfterExceptionAndOuter()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_BEFORE_PROCEED;

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> AroundInjector.create().service().echo( "a" ) );

    assertEquals( exception.getMessage(), "innerAround-before" );
    assertTrue( c_trace.contains( "innerAround:exception:IllegalStateException:innerAround-before" ) );
    assertTrue( c_trace.contains( "outerAround:around:catch:IllegalStateException:innerAround-before" ) );
    assertTrue( c_trace.contains( "outerAround:exception:IllegalStateException:innerAround-before" ) );
  }

  @Test
  public void ownAroundFailureAfterProceedIsObservedByOwnAfterExceptionAndOuter()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_AFTER_PROCEED;

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> AroundInjector.create().service().echo( "a" ) );

    assertEquals( exception.getMessage(), "innerAround-after" );
    assertTrue( c_trace.contains( "target:echo:a" ) );
    assertTrue( c_trace.contains( "innerAround:around:afterProceed:target:a" ) );
    assertTrue( c_trace.contains( "innerAround:exception:IllegalStateException:innerAround-after" ) );
    assertTrue( c_trace.contains( "outerAround:exception:IllegalStateException:innerAround-after" ) );
  }

  @Test
  public void innerFailureThroughProceedIsObservedByOuterAround()
  {
    resetAroundState();

    final IllegalStateException exception =
      expectThrows( IllegalStateException.class, () -> AroundInjector.create().service().runtime() );

    assertEquals( exception.getMessage(), "target-runtime" );
    assertTrue( c_trace.contains( "innerAround:around:catch:IllegalStateException:target-runtime" ) );
    assertTrue( c_trace.contains( "outerAround:around:catch:IllegalStateException:target-runtime" ) );
  }

  @Test
  public void multipleProceedCallsInvokeTargetMultipleTimes()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.DOUBLE_PROCEED;

    assertEquals( AroundInjector.create().service().echo( "a" ), "target:a" );

    assertTrue( c_trace.contains( "innerAround:around:firstProceed:target:a" ) );
    assertEquals( c_trace.stream().filter( "target:echo:a"::equals ).count(), 2 );
    assertFalse( c_trace.stream().anyMatch( e -> e.startsWith( "innerAround:exception:" ) ) );
    assertFalse( c_trace.stream().anyMatch( e -> e.startsWith( "outerAround:exception:" ) ) );
  }

  @Test
  public void replacementArgumentsFlowToInnerInterceptorsAndTarget()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.REPLACE_ARGUMENTS;

    assertEquals( AroundInjector.create().service().replace( "original", 1 ), "replacement:2" );

    assertTrue( c_trace.contains( "innerAround:around:enter:AroundService.replace:inner:[replacement, 2]" ) );
    assertTrue( c_trace.contains( "target:replace:replacement:2" ) );
  }

  @Test
  public void replacementArgumentValidation()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.NULL_REPLACEMENT;
    expectThrows( AssertionError.class, () -> AroundInjector.create().service().replace( "original", 1 ) );

    resetAroundState();
    c_outerAroundMode = AroundMode.WRONG_COUNT_REPLACEMENT;
    expectThrows( AssertionError.class, () -> AroundInjector.create().service().replace( "original", 1 ) );

    resetAroundState();
    c_outerAroundMode = AroundMode.WRONG_COUNT_THEN_PROCEED;
    assertEquals( AroundInjector.create().service().replace( "original", 1 ), "fixed:3" );
    assertTrue( c_trace.contains( "outerAround:wrong-count" ) );
    assertTrue( c_trace.contains( "target:replace:fixed:3" ) );
  }

  @Test
  public void varargsReplacementUsesFormalParameterCount()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.VARARGS_REPLACE;
    assertEquals( AroundInjector.create().service().varargs( "p", "a" ), "changed:x,y" );

    resetAroundState();
    c_outerAroundMode = AroundMode.VARARGS_WRONG_COUNT;
    expectThrows( AssertionError.class, () -> AroundInjector.create().service().varargs( "p", "a" ) );
  }

  @Test
  public void wrongReplacementElementTypesFailNaturally()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.WRONG_TYPE_REPLACEMENT;
    expectThrows( ClassCastException.class, () -> AroundInjector.create().service().replace( "original", 1 ) );

    resetAroundState();
    c_outerAroundMode = AroundMode.NULL_PRIMITIVE_REPLACEMENT;
    expectThrows( NullPointerException.class, () -> AroundInjector.create().service().primitiveArg( 1 ) );
  }

  @Test
  public void voidAroundReturnValueIsIgnored()
  {
    resetAroundState();
    c_outerAroundMode = AroundMode.VOID_SHORT_CIRCUIT_VALUE;

    AroundInjector.create().service().none( "a" );

    assertEquals( c_trace,
                  List.of( "outerAround:before",
                           "outerAround:around:enter:AroundService.none:outer:[a]",
                           "outerAround:after:null" ) );
  }

  @Test
  public void primitiveAroundReturnHandling()
  {
    resetAroundState();
    assertEquals( AroundInjector.create().service().primitive( "abc" ), 3 );

    resetAroundState();
    c_outerAroundMode = AroundMode.RETURN_NULL;
    expectThrows( NullPointerException.class, () -> AroundInjector.create().service().primitive( "abc" ) );

    resetAroundState();
    c_outerAroundMode = AroundMode.RETURN_WRONG_WRAPPER;
    expectThrows( ClassCastException.class, () -> AroundInjector.create().service().primitive( "abc" ) );
  }

  @Test
  public void declaredCheckedExceptionFromAroundIsRethrown()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_DECLARED;

    final IOException exception = expectThrows( IOException.class, () -> AroundInjector.create().service().checked() );

    assertEquals( exception.getMessage(), "innerAround-declared" );
  }

  @Test
  public void undeclaredCheckedThrowableFromAroundIsWrappedAtProxyBoundary()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_UNDECLARED;

    final UndeclaredThrowableException exception =
      expectThrows( UndeclaredThrowableException.class, () -> AroundInjector.create().service().echo( "a" ) );

    assertEquals( exception.getCause().getClass(), Exception.class );
    assertEquals( exception.getCause().getMessage(), "innerAround-undeclared" );
    assertTrue( c_trace.contains( "innerAround:exception:Exception:innerAround-undeclared" ) );
    assertTrue( c_trace.contains( "outerAround:exception:Exception:innerAround-undeclared" ) );
  }

  @Test
  public void runtimeExceptionAndErrorFromAroundRethrowUnchanged()
  {
    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_RUNTIME;
    final IllegalStateException runtime =
      expectThrows( IllegalStateException.class, () -> AroundInjector.create().service().echo( "a" ) );
    assertEquals( runtime.getMessage(), "innerAround-runtime" );

    resetAroundState();
    c_innerAroundMode = AroundMode.THROW_ERROR;
    final AssertionError error = expectThrows( AssertionError.class, () -> AroundInjector.create().service().echo( "a" ) );
    assertEquals( error.getMessage(), "innerAround-error" );
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
