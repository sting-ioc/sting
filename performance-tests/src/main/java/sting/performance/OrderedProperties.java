package sting.performance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * A simple customization of Properties that has a stable output order based on alphabetic ordering of keys.
 */
public final class OrderedProperties
  extends Properties
{
  @Nonnull
  static OrderedProperties load( @Nonnull final Path path )
    throws IOException
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.load( Files.newBufferedReader( path ) );
    return properties;
  }

  @Override
  public synchronized Enumeration<Object> keys()
  {
    return Collections.enumeration( keySet() );
  }

  @Nonnull
  @Override
  public Set<Object> keySet()
  {
    // Used in Java8 when writing properties
    return new TreeSet<>( super.keySet() );
  }

  @Nonnull
  @Override
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    // Used in Java17+ when writing properties
    return new TreeMap<>( this ).entrySet();
  }

  void removeWithPrefix( @Nonnull final String prefix )
  {
    keySet()
      .stream()
      .filter( k -> ( (String) k ).startsWith( prefix ) )
      .collect( Collectors.toList() )
      .forEach( this::remove );
  }

  void mergeWithPrefix( @Nonnull final Properties properties, @Nonnull final String prefix )
  {
    properties.forEach( ( key, value ) -> put( prefix + key, value ) );
  }
}
