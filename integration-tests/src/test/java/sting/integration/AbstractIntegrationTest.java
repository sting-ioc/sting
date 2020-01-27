package sting.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public abstract class AbstractIntegrationTest
{
  @Nonnull
  private static final List<String> c_trace = new ArrayList<>();

  public static abstract class BaseModel
  {
    protected BaseModel( final Object... args )
    {
      c_trace.add( toString() + Arrays.asList( args ) );
    }

    @Override
    public String toString()
    {
      return getClass().getSimpleName();
    }
  }

  @BeforeMethod
  protected void preTest()
  {
    clearTrace();
  }

  protected final void clearTrace()
  {
    c_trace.clear();
  }

  protected final void assertCreateTrace( @Nonnull final String trace )
  {
    assertEquals( String.join( " ", c_trace ), trace );
  }
}
