package sting.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

final class InjectorDotReportGenerator
{
  private InjectorDotReportGenerator()
  {
  }

  @Nonnull
  static String buildDotReport( @Nonnull final ProcessingEnvironment processingEnv,
                                @Nonnull final ComponentGraph graph )
  {
    final Map<String, Set<String>> types = buildTypeMap( graph );

    final StringBuilder sb = new StringBuilder();
    final String injectorName =
      extractShortestUniqueName( types, graph.getInjector().getElement().asType().toString() );
    sb.append( "digraph \"" )
      .append( injectorName )
      .append( "\" {\n" )
      .append( "  overlap = false\n" )
      .append( "  splines = true\n" );

    sb.append( "  injector [label=\"" )
      .append( injectorName )
      .append( "\",color=green];\n" );

    for ( final Node node : graph.getRawNodeCollection() )
    {
      sb.append( "  " )
        .append( node.getName() )
        .append( " [label=\"" )
        .append( extractShortestUniqueName( types, node.getType().toString() ) );

      final List<ServiceSpec> services = node.getBinding().getPublishedServices();
      if ( !services.isEmpty() )
      {
        final String qualifier = services.get( 0 ).getCoordinate().getQualifier();
        if ( !"".equals( qualifier ) )
        {
          sb.append( "/" ).append( qualifier );
        }
      }

      sb.append( "\"" );
      if ( node.isEager() )
      {
        sb.append( ",color=blue" );
      }
      sb.append( "];\n" );
    }

    emitDependencyLinks( processingEnv, types, sb, graph.getRootNode(), "injector" );
    for ( final Node node : graph.getRawNodeCollection() )
    {
      emitDependencyLinks( processingEnv, types, sb, node, node.getName() );
    }
    sb.append( "}\n" );
    return sb.toString();
  }

  private static void emitDependencyLinks( @Nonnull final ProcessingEnvironment processingEnv,
                                           @Nonnull final Map<String, Set<String>> types,
                                           @Nonnull final StringBuilder sb,
                                           @Nonnull final Node node,
                                           @Nonnull final String fromName )
  {
    for ( final Edge edge : node.getDependsOn() )
    {
      emitNodeLinks( processingEnv, types, sb, edge, fromName );
    }
  }

  private static void emitNodeLinks( @Nonnull final ProcessingEnvironment processingEnv,
                                     @Nonnull final Map<String, Set<String>> types,
                                     @Nonnull final StringBuilder sb,
                                     @Nonnull final Edge edge,
                                     @Nonnull final String fromName )
  {
    for ( final Node other : edge.getSatisfiedBy() )
    {
      emitNodeLink( processingEnv, types, sb, edge, other, fromName );
    }
  }

  private static void emitNodeLink( @Nonnull final ProcessingEnvironment processingEnv,
                                    @Nonnull final Map<String, Set<String>> types,
                                    @Nonnull final StringBuilder sb,
                                    @Nonnull final Edge edge,
                                    @Nonnull final Node toNode,
                                    @Nonnull final String fromName )
  {
    sb.append( "  " )
      .append( fromName )
      .append( " -> " )
      .append( toNode.getName() )
      .append( " [" );
    boolean hasAttributes = false;
    final ServiceDescriptor service = edge.getService();
    final ServiceSpec serviceSpec = service.getService();
    final Coordinate coordinate = serviceSpec.getCoordinate();
    final TypeMirror serviceType = coordinate.getType();
    if ( !processingEnv.getTypeUtils().isSameType( serviceType, toNode.getType() ) )
    {
      sb.append( "label=\"" )
        .append( extractShortestUniqueName( types, serviceType.toString() ) );
      final String qualifier = coordinate.getQualifier();
      if ( !"".equals( qualifier ) )
      {
        sb.append( "/" ).append( qualifier );
      }
      sb.append( "\"" );
      hasAttributes = true;
    }
    if ( serviceSpec.isOptional() )
    {
      if ( hasAttributes )
      {
        sb.append( "," );
      }
      sb.append( "style=dotted" );
      hasAttributes = true;
    }
    if ( service.getKind().isCollection() && service.getKind().isSupplier() )
    {
      if ( hasAttributes )
      {
        sb.append( "," );
      }
      sb.append( "dir=both, arrowtail=odot, arrowhead=crow" );
      hasAttributes = true;
    }
    else if ( service.getKind().isCollection() )
    {
      if ( hasAttributes )
      {
        sb.append( "," );
      }
      sb.append( "dir=both, arrowtail=normal, arrowhead=crow" );
      hasAttributes = true;
    }
    else if ( service.getKind().isSupplier() )
    {
      if ( hasAttributes )
      {
        sb.append( "," );
      }
      sb.append( "arrowhead=odot" );
      //noinspection UnusedAssignment
      hasAttributes = true;
    }
    sb.append( "];\n" );
  }

  @Nonnull
  private static Map<String, Set<String>> buildTypeMap( @Nonnull final ComponentGraph graph )
  {
    // Map used to try and generate the shortest name for a node
    // SimpleName -> [FQN]
    final Map<String, Set<String>> types = new HashMap<>();
    for ( final Node node : graph.getRawNodeCollection() )
    {
      recordType( types, node.getType().toString() );
      recordDependencyTypes( types, node );
    }
    recordType( types, graph.getInjector().getElement().asType().toString() );
    recordDependencyTypes( types, graph.getRootNode() );
    return types;
  }

  private static void recordDependencyTypes( @Nonnull final Map<String, Set<String>> types, @Nonnull final Node node )
  {
    for ( final Edge edge : node.getDependsOn() )
    {
      recordType( types, edge.getService().getService().getCoordinate().getType().toString() );
    }
  }

  private static void recordType( @Nonnull final Map<String, Set<String>> types, @Nonnull final String type )
  {
    types.computeIfAbsent( extractSimpleName( type ), v -> new HashSet<>() ).add( type );
  }

  @Nonnull
  private static String extractShortestUniqueName( @Nonnull final Map<String, Set<String>> types,
                                                   @Nonnull final String typeName )
  {
    final String simpleName = extractSimpleName( typeName );
    final Set<String> matches = types.get( simpleName );
    if ( 1 == matches.size() )
    {
      return simpleName;
    }
    else
    {
      final List<String> parts = new ArrayList<>();
      parts.add( simpleName );

      boolean matched = true;
      int offset = 1;
      while ( matched )
      {
        String match = null;
        for ( final String type : matches )
        {
          final String[] typeParts = type.split( "\\." );
          if ( typeParts.length <= offset )
          {
            match = null;
            matched = false;
            break;
          }
          else if ( null == match )
          {
            match = typeParts[ typeParts.length - 1 - offset ];
          }
          else if ( !match.equals( typeParts[ typeParts.length - 1 - offset ] ) )
          {
            match = null;
            matched = false;
            break;
          }
        }
        if ( null != match )
        {
          parts.add( 0, match );
          offset++;
        }
      }
      final String[] typeParts = typeName.split( "\\." );
      parts.add( 0, typeParts[ typeParts.length - 1 - offset ] );
      return "..." + String.join( ".", parts );
    }
  }

  @Nonnull
  private static String extractSimpleName( @Nonnull final String type )
  {
    final String[] parts = type.split( "\\." );
    return parts[ parts.length - 1 ];
  }
}
