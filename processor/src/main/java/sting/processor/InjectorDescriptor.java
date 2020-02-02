package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

final class InjectorDescriptor
{
  /**
   * The element that defined the Injector.
   * It may be either an interface or an abstract class.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * The list of types included by Injector.
   */
  @Nonnull
  private final Collection<DeclaredType> _includes;
  /**
   * The collection of services that must be supplied to the injector during creation.
   */
  @Nonnull
  private final List<DependencyDescriptor> _inputs;
  /**
   * The collection of services made available from the injector.
   */
  @Nonnull
  private final List<DependencyDescriptor> _outputs;

  InjectorDescriptor( @Nonnull final TypeElement element,
                      @Nonnull final Collection<DeclaredType> includes,
                      @Nonnull final List<DependencyDescriptor> inputs,
                      @Nonnull final List<DependencyDescriptor> outputs )
  {
    _element = Objects.requireNonNull( element );
    _includes = Objects.requireNonNull( includes );
    _inputs = Objects.requireNonNull( inputs );
    _outputs = Objects.requireNonNull( outputs );
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  @Nonnull
  Collection<DeclaredType> getIncludes()
  {
    return _includes;
  }

  @Nonnull
  List<DependencyDescriptor> getInputs()
  {
    return _inputs;
  }

  @Nonnull
  List<DependencyDescriptor> getOutputs()
  {
    return _outputs;
  }

  void write( final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "injector/1" );
    if ( !_includes.isEmpty() )
    {
      g.writeStartArray( "includes" );
      for ( final DeclaredType include : _includes )
      {
        g.write( include.toString() );
      }
      g.writeEnd();
    }
    if ( !_inputs.isEmpty() )
    {
      g.writeStartArray( "inputs" );
      for ( final DependencyDescriptor dependency : _inputs )
      {
        dependency.write( g );
      }
      g.writeEnd();
    }
    if ( !_outputs.isEmpty() )
    {
      g.writeStartArray( "outputs" );
      for ( final DependencyDescriptor dependency : _outputs )
      {
        dependency.write( g );
      }
      g.writeEnd();
    }
    g.writeEnd();
  }
}
