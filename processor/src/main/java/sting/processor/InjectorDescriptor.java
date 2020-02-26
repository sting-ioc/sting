package sting.processor;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;

final class InjectorDescriptor
{
  /**
   * The element that defined the Injector.
   * It may be either an interface or an abstract class.
   */
  @Nonnull
  private final TypeElement _element;
  /**
   * Should the injector be optimized for GWT.
   */
  private final boolean _gwt;
  /**
   * Should this injector be able to be included i other injectors.
   */
  private final boolean _injectable;
  /**
   * The list of types included by Injector.
   */
  @Nonnull
  private final Collection<IncludeDescriptor> _includes;
  /**
   * The collection of services that must be supplied to the injector during creation.
   */
  @Nonnull
  private final List<InputDescriptor> _inputs;
  /**
   * The collection of services made available from the injector.
   */
  @Nonnull
  private final List<ServiceDescriptor> _outputs;
  /**
   * True if the injector has a fatal error and should not be reprocessed.
   */
  private boolean _containsError;

  InjectorDescriptor( @Nonnull final TypeElement element,
                      final boolean gwt,
                      final boolean injectable,
                      @Nonnull final Collection<IncludeDescriptor> includes,
                      @Nonnull final List<InputDescriptor> inputs,
                      @Nonnull final List<ServiceDescriptor> outputs )
  {
    _element = Objects.requireNonNull( element );
    _gwt = gwt;
    _injectable = injectable;
    _includes = Objects.requireNonNull( includes );
    _inputs = Objects.requireNonNull( inputs );
    _outputs = Objects.requireNonNull( outputs );
  }

  @Nonnull
  TypeElement getElement()
  {
    return _element;
  }

  boolean isGwt()
  {
    return _gwt;
  }

  boolean isInjectable()
  {
    return _injectable;
  }

  @Nonnull
  Collection<IncludeDescriptor> getIncludes()
  {
    return _includes;
  }

  @Nonnull
  List<InputDescriptor> getInputs()
  {
    return _inputs;
  }

  @Nonnull
  List<ServiceDescriptor> getOutputs()
  {
    return _outputs;
  }

  boolean containsError()
  {
    return _containsError;
  }

  void markAsContainsError()
  {
    _containsError = true;
  }

  void write( final JsonGenerator g )
  {
    g.writeStartObject();
    g.write( "schema", "injector/1" );
    if ( _injectable )
    {
      g.write( "injectable", "true" );
    }
    if ( !_includes.isEmpty() )
    {
      g.writeStartArray( "includes" );
      for ( final IncludeDescriptor include : _includes )
      {
        g.write( include.getIncludedType().toString() );
      }
      g.writeEnd();
    }
    if ( !_inputs.isEmpty() )
    {
      g.writeStartArray( "inputs" );
      for ( final InputDescriptor input : _inputs )
      {
        input.write( g );
      }
      g.writeEnd();
    }
    if ( !_outputs.isEmpty() )
    {
      g.writeStartArray( "outputs" );
      for ( final ServiceDescriptor dependency : _outputs )
      {
        dependency.write( g );
      }
      g.writeEnd();
    }
    g.writeEnd();
  }
}
