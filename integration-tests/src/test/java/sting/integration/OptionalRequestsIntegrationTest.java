package sting.integration;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injectable;
import sting.Injector;
import sting.Typed;
import static org.testng.Assert.*;

public final class OptionalRequestsIntegrationTest
  extends AbstractIntegrationTest
{
  private static int c_requiredServiceCreations;
  private static int c_requiredPluginCreations;
  private static int c_optionalPluginCreations;

  public interface Plugin
  {
  }

  public static final class MissingService
  {
  }

  public static class RequiredService
  {
    RequiredService()
    {
      c_requiredServiceCreations++;
    }
  }

  public static final class RequiredPlugin
    implements Plugin
  {
    RequiredPlugin()
    {
      c_requiredPluginCreations++;
    }
  }

  public static final class OptionalPlugin
    implements Plugin
  {
    OptionalPlugin()
    {
      c_optionalPluginCreations++;
    }
  }

  @Injectable
  public static final class Consumer
  {
    @Nonnull
    private final Optional<RequiredService> _requiredService;
    @Nonnull
    private final Collection<Plugin> _plugins;
    @Nonnull
    private final Supplier<Optional<Runnable>> _optionalRunnableSupplier;
    @Nonnull
    private final Collection<Supplier<Optional<Plugin>>> _pluginSuppliers;

    Consumer( @Nonnull final Optional<RequiredService> requiredService,
              @Nonnull final Collection<Plugin> plugins,
              @Nonnull final Supplier<Optional<Runnable>> optionalRunnableSupplier,
              @Nonnull final Collection<Supplier<Optional<Plugin>>> pluginSuppliers )
    {
      _requiredService = requiredService;
      _plugins = plugins;
      _optionalRunnableSupplier = optionalRunnableSupplier;
      _pluginSuppliers = pluginSuppliers;
    }

    @Nonnull
    Optional<RequiredService> getRequiredService()
    {
      return _requiredService;
    }

    @Nonnull
    Collection<Plugin> getPlugins()
    {
      return _plugins;
    }

    @Nonnull
    Supplier<Optional<Runnable>> getOptionalRunnableSupplier()
    {
      return _optionalRunnableSupplier;
    }

    @Nonnull
    Collection<Supplier<Optional<Plugin>>> getPluginSuppliers()
    {
      return _pluginSuppliers;
    }
  }

  @Injector( fragmentOnly = false, includes = { RequiredServiceNode.class, OptionalRequestsFragment.class } )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new OptionalRequestsIntegrationTest_Sting_MyInjector();
    }

    Consumer getConsumer();

    String getSummary();

    Optional<RequiredService> getRequiredService();

    Optional<MissingService> getMissingService();

    Optional<Runnable> getOptionalRunnable();

    Supplier<Optional<Runnable>> getOptionalRunnableSupplier();

    Collection<Plugin> getPlugins();

    Collection<Supplier<Optional<Plugin>>> getPluginSuppliers();
  }

  @Injectable
  @Typed( RequiredService.class )
  public static final class RequiredServiceNode
    extends RequiredService
  {
  }

  @Fragment
  public interface OptionalRequestsFragment
  {
    default Plugin provideRequiredPlugin()
    {
      return new RequiredPlugin();
    }

    @Nullable
    default Plugin provideNullablePlugin()
    {
      return null;
    }

    @Nullable
    default Plugin provideOptionalPlugin()
    {
      return new OptionalPlugin();
    }

    @Nullable
    default Runnable provideOptionalRunnable()
    {
      return null;
    }

    default String provideSummary( Collection<Plugin> plugins )
    {
      return Integer.toString( plugins.size() );
    }
  }

  @Test
  public void optionalRequests()
  {
    resetCounters();
    final MyInjector injector = MyInjector.create();

    assertTrue( injector.getMissingService().isEmpty() );
    assertEquals( c_requiredServiceCreations, 0 );

    assertTrue( injector.getRequiredService().isPresent() );
    assertEquals( c_requiredServiceCreations, 1 );

    assertTrue( injector.getOptionalRunnable().isEmpty() );
    assertTrue( injector.getOptionalRunnableSupplier().get().isEmpty() );

    resetPluginCounters();
    final Collection<Plugin> plugins = injector.getPlugins();
    assertEquals( plugins.size(), 2 );
    assertEquals( c_requiredPluginCreations, 1 );
    assertEquals( c_optionalPluginCreations, 1 );

    resetPluginCounters();
    final MyInjector supplierInjector = MyInjector.create();
    final Collection<Supplier<Optional<Plugin>>> pluginSuppliers = supplierInjector.getPluginSuppliers();
    assertEquals( pluginSuppliers.size(), 3 );
    assertEquals( c_requiredPluginCreations, 0 );
    assertEquals( c_optionalPluginCreations, 0 );
    int present = 0;
    int empty = 0;
    for ( final Supplier<Optional<Plugin>> pluginSupplier : pluginSuppliers )
    {
      if ( pluginSupplier.get().isPresent() )
      {
        present++;
      }
      else
      {
        empty++;
      }
    }
    assertEquals( present, 2 );
    assertEquals( empty, 1 );
    assertEquals( c_requiredPluginCreations, 1 );
    assertEquals( c_optionalPluginCreations, 1 );

    resetCounters();
    final MyInjector injector2 = MyInjector.create();
    final Consumer consumer = injector2.getConsumer();
    assertTrue( consumer.getRequiredService().isPresent() );
    assertEquals( consumer.getPlugins().size(), 2 );
    assertTrue( consumer.getOptionalRunnableSupplier().get().isEmpty() );
    present = 0;
    empty = 0;
    for ( final Supplier<Optional<Plugin>> pluginSupplier : consumer.getPluginSuppliers() )
    {
      if ( pluginSupplier.get().isPresent() )
      {
        present++;
      }
      else
      {
        empty++;
      }
    }
    assertEquals( present, 2 );
    assertEquals( empty, 1 );
    assertEquals( c_requiredServiceCreations, 1 );
    assertEquals( c_requiredPluginCreations, 1 );
    assertEquals( c_optionalPluginCreations, 1 );

    resetPluginCounters();
    final MyInjector summaryInjector = MyInjector.create();
    assertEquals( summaryInjector.getSummary(), "2" );
    assertEquals( c_requiredPluginCreations, 1 );
    assertEquals( c_optionalPluginCreations, 1 );
  }

  private void resetCounters()
  {
    c_requiredServiceCreations = 0;
    resetPluginCounters();
  }

  private void resetPluginCounters()
  {
    c_requiredPluginCreations = 0;
    c_optionalPluginCreations = 0;
  }
}
