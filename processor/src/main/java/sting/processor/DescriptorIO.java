package sting.processor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

final class DescriptorIO
{
  private static final int FILE_HEADER = 0x2187;
  private static final int FILE_VERSION = 1;
  private static final int INJECTABLE_TAG = 0;
  private static final int FRAGMENT_TAG = 1;
  @Nonnull
  private final Elements _elements;
  @Nonnull
  private final Types _types;

  DescriptorIO( @Nonnull final Elements elements, @Nonnull final Types types )
  {
    _elements = Objects.requireNonNull( elements );
    _types = Objects.requireNonNull( types );
  }

  @Nonnull
  Object read( @Nonnull final DataInputStream dis, @Nonnull final String classname )
    throws IOException
  {
    final int header = dis.readInt();
    if ( FILE_HEADER != header )
    {
      throw new IOException( "Descriptor for " + classname + " is in an incorrect format. Bad header." );
    }
    final int version = dis.readShort();
    if ( FILE_VERSION != version )
    {
      throw new IOException( "Descriptor for " + classname + " is in an unknown version: " + version );
    }
    final TypeElement typeElement = _elements.getTypeElement( classname );
    assert null != typeElement;
    final byte tag = dis.readByte();
    final Object descriptor;
    if ( FRAGMENT_TAG == tag )
    {
      descriptor = readFragment( dis, typeElement );
    }
    else
    {
      assert INJECTABLE_TAG == tag;
      descriptor = readInjectable( dis, typeElement );
    }
    return descriptor;
  }

  void write( @Nonnull final DataOutputStream dos, @Nonnull final Object descriptor )
    throws IOException
  {
    dos.writeInt( FILE_HEADER );
    dos.writeShort( FILE_VERSION );
    if ( descriptor instanceof FragmentDescriptor )
    {
      dos.writeByte( FRAGMENT_TAG );
      writeFragment( dos, (FragmentDescriptor) descriptor );
    }
    else
    {
      assert descriptor instanceof InjectableDescriptor;
      dos.writeByte( INJECTABLE_TAG );
      writeInjectable( dos, (InjectableDescriptor) descriptor );
    }
  }

  private void writeFragment( @Nonnull final DataOutputStream dos, @Nonnull final FragmentDescriptor fragment )
    throws IOException
  {
    final Collection<IncludeDescriptor> includes = fragment.getIncludes();
    dos.writeShort( includes.size() );
    for ( final IncludeDescriptor include : includes )
    {
      dos.writeUTF( toFieldDescriptor( include.getIncludedType() ) );
      dos.writeUTF( include.getActualTypeName()  );
    }
    final Collection<Binding> bindings = fragment.getBindings();
    dos.writeShort( bindings.size() );
    for ( final Binding binding : bindings )
    {
      writeBinding( dos, binding );
    }
  }

  @Nonnull
  private FragmentDescriptor readFragment( @Nonnull final DataInputStream dis,
                                           @Nonnull final TypeElement enclosingElement )
    throws IOException
  {
    final short includeCount = dis.readShort();
    final IncludeDescriptor[] types = new IncludeDescriptor[ includeCount ];
    for ( int i = 0; i < types.length; i++ )
    {
      final DeclaredType includedType = readDeclaredType( dis.readUTF() );
      final String actualTypeName = dis.readUTF();
      types[ i ] = new IncludeDescriptor( includedType, actualTypeName );
    }
    final short bindingCount = dis.readShort();
    final Binding[] bindings = new Binding[ bindingCount ];
    for ( int i = 0; i < bindings.length; i++ )
    {
      bindings[ i ] = readBinding( dis, enclosingElement );
    }
    final FragmentDescriptor fragment =
      new FragmentDescriptor( enclosingElement, Arrays.asList( types ), Arrays.asList( bindings ) );
    fragment.markJavaStubAsGenerated();
    return fragment;
  }

  private void writeInjectable( @Nonnull final DataOutputStream dos, @Nonnull final InjectableDescriptor injectable )
    throws IOException
  {
    writeBinding( dos, injectable.getBinding() );
  }

  @Nonnull
  private InjectableDescriptor readInjectable( @Nonnull final DataInputStream dis,
                                               @Nonnull final TypeElement enclosingElement )
    throws IOException
  {
    final InjectableDescriptor injectable = new InjectableDescriptor( readBinding( dis, enclosingElement ) );
    injectable.markJavaStubAsGenerated();
    return injectable;
  }

  private void writeBinding( @Nonnull final DataOutputStream dos, @Nonnull final Binding binding )
    throws IOException
  {
    final Binding.Kind kind = binding.getKind();
    dos.writeByte( kind.ordinal() );
    dos.writeUTF( binding.getId() );
    final List<ServiceSpec> services = binding.getPublishedServices();
    dos.writeShort( services.size() );
    for ( final ServiceSpec service : services )
    {
      final Coordinate coordinate = service.getCoordinate();
      dos.writeUTF( coordinate.getQualifier() );
      dos.writeUTF( toFieldDescriptor( coordinate.getType() ) );
      dos.writeBoolean( service.isOptional() );
    }
    dos.writeBoolean( binding.isEager() );
    dos.writeUTF( Binding.Kind.PROVIDES == kind ? binding.getElement().getSimpleName().toString() : "" );
    final ServiceRequest[] dependencies = binding.getDependencies();
    dos.writeShort( dependencies.length );
    for ( final ServiceRequest dependency : dependencies )
    {
      writeService( dos, dependency );
    }
  }

  @Nonnull
  private Binding readBinding( @Nonnull final DataInputStream dis, @Nonnull final TypeElement enclosingElement )
    throws IOException
  {
    final Binding.Kind kind = Binding.Kind.values()[ dis.readByte() ];
    final String id = dis.readUTF();
    final short typeCount = dis.readShort();
    final ServiceSpec[] specs = new ServiceSpec[ typeCount ];
    for ( int i = 0; i < typeCount; i++ )
    {
      specs[ i ] = new ServiceSpec( readCoordinate( dis ), dis.readBoolean() );
    }
    final boolean eager = dis.readBoolean();
    final String elementName = dis.readUTF();
    assert "".equals( elementName ) || Binding.Kind.INJECTABLE != kind;

    final ExecutableElement element;
    if ( Binding.Kind.INJECTABLE != kind )
    {
      element = enclosingElement.getEnclosedElements()
        .stream()
        .filter( e -> ElementKind.METHOD == e.getKind() && e.getSimpleName().toString().equals( elementName ) )
        .map( e -> (ExecutableElement) e )
        .findAny()
        .orElse( null );
    }
    else
    {
      element = enclosingElement.getEnclosedElements()
        .stream()
        .filter( e -> ElementKind.CONSTRUCTOR == e.getKind() )
        .map( e -> (ExecutableElement) e )
        .findAny()
        .orElse( null );
    }
    assert null != element;

    final short dependencyCount = dis.readShort();
    final ServiceRequest[] dependencies = new ServiceRequest[ dependencyCount ];
    for ( int i = 0; i < dependencies.length; i++ )
    {
      dependencies[ i ] = readService( dis, element );
    }

    return new Binding( kind, id, Arrays.asList( specs ), eager, element, dependencies );
  }

  private void writeService( @Nonnull final DataOutputStream dos, @Nonnull final ServiceRequest service )
    throws IOException
  {
    dos.writeByte( service.getKind().ordinal() );
    writeCoordinate( dos, service.getService().getCoordinate() );
    dos.writeBoolean( service.getService().isOptional() );
    assert ElementKind.PARAMETER == service.getElement().getKind();
    // parameter of method in @Fragment type or parameter of constructor in @Injectable type
    // we are not expected to emit binary descriptors for @Injector annotated types and thus do need
    // to handle when "ElementKind.METHOD == service.getElement().getKind()"
    dos.writeShort( service.getParameterIndex() );
  }

  @Nonnull
  private ServiceRequest readService( @Nonnull final DataInputStream dis, @Nonnull final Element enclosingElement )
    throws IOException
  {
    final ServiceRequest.Kind type = ServiceRequest.Kind.values()[ dis.readByte() ];
    final Coordinate coordinate = readCoordinate( dis );
    final boolean optional = dis.readBoolean();
    final short parameterIndex = dis.readShort();
    assert -1 != parameterIndex;
    final Element element = ( (ExecutableElement) enclosingElement ).getParameters().get( parameterIndex );
    assert null != element;

    return new ServiceRequest( type, new ServiceSpec( coordinate, optional ), element, parameterIndex );
  }

  private void writeCoordinate( @Nonnull final DataOutputStream dos, @Nonnull final Coordinate coordinate )
    throws IOException
  {
    dos.writeUTF( coordinate.getQualifier() );
    dos.writeUTF( toFieldDescriptor( coordinate.getType() ) );
  }

  @Nonnull
  private Coordinate readCoordinate( @Nonnull final DataInputStream dis )
    throws IOException
  {
    final String qualifier = dis.readUTF();
    final String type = dis.readUTF();
    return new Coordinate( qualifier, fromFieldDescriptor( type ) );
  }

  /**
   * Emit typeMirror as field descriptors described in section "4.3.2. Field Descriptors" of the JLS.
   */
  @Nonnull
  private String toFieldDescriptor( @Nonnull final TypeMirror typeMirror )
  {
    final TypeKind kind = typeMirror.getKind();
    switch ( kind )
    {
      case BOOLEAN:
        return "Z";
      case CHAR:
        return "C";
      case BYTE:
        return "B";
      case SHORT:
        return "S";
      case INT:
        return "I";
      case LONG:
        return "J";
      case FLOAT:
        return "F";
      case DOUBLE:
        return "D";
      default:
        assert TypeKind.DECLARED == kind;
        // Injected types do NOT contain arrays and thus the only other valid parameterized type is DECLARED
        return "L" + _elements.getBinaryName( (TypeElement) _types.asElement( typeMirror ) ) + ";";
    }
  }

  /**
   * Parse a subset of field descriptors to TypeMirror.
   */
  @Nonnull
  private TypeMirror fromFieldDescriptor( @Nonnull final String descriptor )
  {
    switch ( descriptor )
    {
      case "Z":
        return _types.getPrimitiveType( TypeKind.BOOLEAN );
      case "C":
        return _types.getPrimitiveType( TypeKind.CHAR );
      case "B":
        return _types.getPrimitiveType( TypeKind.BYTE );
      case "S":
        return _types.getPrimitiveType( TypeKind.SHORT );
      case "I":
        return _types.getPrimitiveType( TypeKind.INT );
      case "J":
        return _types.getPrimitiveType( TypeKind.LONG );
      case "F":
        return _types.getPrimitiveType( TypeKind.FLOAT );
      case "D":
        return _types.getPrimitiveType( TypeKind.DOUBLE );
      default:
        return readDeclaredType( descriptor );
    }
  }

  @Nonnull
  private DeclaredType readDeclaredType( @Nonnull final String descriptor )
  {
    assert descriptor.startsWith( "L" );
    assert descriptor.endsWith( ";" );
    // Injected types do NOT contain arrays and thus the only other valid parameterized type is DECLARED
    final String classname = descriptor.substring( 1, descriptor.length() - 1 ).replace( "$", "." );
    final TypeElement typeElement = _elements.getTypeElement( classname );
    assert null != typeElement;
    return (DeclaredType) typeElement.asType();
  }
}
