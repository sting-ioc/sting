package sting.integration;

import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import sting.Fragment;
import sting.Injector;
import static org.testng.Assert.*;

public final class PrimitiveBoxedInteropIntegrationTest
  extends AbstractIntegrationTest
{
  @Fragment
  public interface MyFragment
  {
    default boolean provideValue1()
    {
      return true;
    }

    default char provideValue2()
    {
      return 'Q';
    }

    default byte provideValue3()
    {
      return 63;
    }

    default short provideValue4()
    {
      return 64;
    }

    default int provideValue5()
    {
      return 65;
    }

    default long provideValue6()
    {
      return 66L;
    }

    default float provideValue7()
    {
      return 67.5F;
    }

    default double provideValue8()
    {
      return 68.5D;
    }
  }

  @Injector( includes = MyFragment.class )
  public interface MyInjector
  {
    @Nonnull
    static MyInjector create()
    {
      return new PrimitiveBoxedInteropIntegrationTest_Sting_MyInjector();
    }

    boolean getPrimitiveValue1();

    char getPrimitiveValue2();

    byte getPrimitiveValue3();

    short getPrimitiveValue4();

    int getPrimitiveValue5();

    long getPrimitiveValue6();

    float getPrimitiveValue7();

    double getPrimitiveValue8();

    Boolean getBoxedValue1();

    Character getBoxedValue2();

    Byte getBoxedValue3();

    Short getBoxedValue4();

    Integer getBoxedValue5();

    Long getBoxedValue6();

    Float getBoxedValue7();

    Double getBoxedValue8();

    Collection<Boolean> getValue1s();

    Collection<Character> getValue2s();

    Collection<Byte> getValue3s();

    Collection<Short> getValue4s();

    Collection<Integer> getValue5s();

    Collection<Long> getValue6s();

    Collection<Float> getValue7s();

    Collection<Double> getValue8s();

    Collection<Supplier<Boolean>> getValue1Suppliers();

    Collection<Supplier<Character>> getValue2Suppliers();

    Collection<Supplier<Byte>> getValue3Suppliers();

    Collection<Supplier<Short>> getValue4Suppliers();

    Collection<Supplier<Integer>> getValue5Suppliers();

    Collection<Supplier<Long>> getValue6Suppliers();

    Collection<Supplier<Float>> getValue7Suppliers();

    Collection<Supplier<Double>> getValue8Suppliers();
  }

  @Test
  public void primitiveOutputs()
  {
    final MyInjector injector = MyInjector.create();

    assertTrue( injector.getPrimitiveValue1() );
    assertEquals( injector.getPrimitiveValue2(), 'Q' );
    assertEquals( injector.getPrimitiveValue3(), (byte) 63 );
    assertEquals( injector.getPrimitiveValue4(), (short) 64 );
    assertEquals( injector.getPrimitiveValue5(), 65 );
    assertEquals( injector.getPrimitiveValue6(), 66L );
    assertEquals( injector.getPrimitiveValue7(), 67.5F, 0.0F );
    assertEquals( injector.getPrimitiveValue8(), 68.5D, 0.0D );
  }

  @Test
  public void boxedOutputs()
  {
    final MyInjector injector = MyInjector.create();

    assertEquals( injector.getBoxedValue1(), Boolean.TRUE );
    assertEquals( injector.getBoxedValue2(), Character.valueOf( 'Q' ) );
    assertEquals( injector.getBoxedValue3(), Byte.valueOf( (byte) 63 ) );
    assertEquals( injector.getBoxedValue4(), Short.valueOf( (short) 64 ) );
    assertEquals( injector.getBoxedValue5(), Integer.valueOf( 65 ) );
    assertEquals( injector.getBoxedValue6(), Long.valueOf( 66L ) );
    assertEquals( injector.getBoxedValue7(), Float.valueOf( 67.5F ) );
    assertEquals( injector.getBoxedValue8(), Double.valueOf( 68.5D ) );
  }

  @Test
  public void collectionOutputs()
  {
    final MyInjector injector = MyInjector.create();

    assertSingletonCollection( injector.getValue1s(), Boolean.TRUE );
    assertSingletonCollection( injector.getValue2s(), 'Q' );
    assertSingletonCollection( injector.getValue3s(), (byte) 63 );
    assertSingletonCollection( injector.getValue4s(), (short) 64 );
    assertSingletonCollection( injector.getValue5s(), 65 );
    assertSingletonCollection( injector.getValue6s(), 66L );
    assertSingletonCollection( injector.getValue7s(), 67.5F );
    assertSingletonCollection( injector.getValue8s(), 68.5D );
  }

  @Test
  public void supplierCollectionOutputs()
  {
    final MyInjector injector = MyInjector.create();

    assertSingletonSupplierCollection( injector.getValue1Suppliers(), Boolean.TRUE );
    assertSingletonSupplierCollection( injector.getValue2Suppliers(), 'Q' );
    assertSingletonSupplierCollection( injector.getValue3Suppliers(), (byte) 63 );
    assertSingletonSupplierCollection( injector.getValue4Suppliers(), (short) 64 );
    assertSingletonSupplierCollection( injector.getValue5Suppliers(), 65 );
    assertSingletonSupplierCollection( injector.getValue6Suppliers(), 66L );
    assertSingletonSupplierCollection( injector.getValue7Suppliers(), 67.5F );
    assertSingletonSupplierCollection( injector.getValue8Suppliers(), 68.5D );
  }

  private <T> void assertSingletonCollection( @Nonnull final Collection<T> values, @Nonnull final T value )
  {
    assertEquals( values.size(), 1 );
    assertEquals( values.iterator().next(), value );
  }

  private <T> void assertSingletonSupplierCollection( @Nonnull final Collection<Supplier<T>> suppliers,
                                                      @Nonnull final T value )
  {
    assertEquals( suppliers.size(), 1 );
    assertEquals( suppliers.iterator().next().get(), value );
  }
}
