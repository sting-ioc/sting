package sting.processor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
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
    final TypeElement typeElement = _elements.getTypeElement( classname );
    assert null != typeElement;
    final byte tag = dis.readByte();
    if ( FRAGMENT_TAG == tag )
    {
      return readFragment( dis, typeElement );
    }
    else
    {
      assert INJECTABLE_TAG == tag;
      return readInjectable( dis, typeElement );
    }
  }

  void write( @Nonnull final DataOutputStream dos, @Nonnull final Object descriptor )
    throws IOException
  {
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
    final Collection<DeclaredType> includes = fragment.getIncludes();
    dos.writeShort( includes.size() );
    for ( final DeclaredType include : includes )
    {
      dos.writeUTF( toFieldDescriptor( include ) );
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
    final DeclaredType[] types = new DeclaredType[ includeCount ];
    for ( int i = 0; i < types.length; i++ )
    {
      types[ i ] = readDeclaredType( dis.readUTF() );
    }
    final short bindingCount = dis.readShort();
    final Binding[] bindings = new Binding[ bindingCount ];
    for ( int i = 0; i < bindings.length; i++ )
    {
      bindings[ i ] = readBinding( dis, enclosingElement );
    }
    return new FragmentDescriptor( enclosingElement, Arrays.asList( types ), Arrays.asList( bindings ) );
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
    return new InjectableDescriptor( readBinding( dis, enclosingElement ) );
  }

  private void writeBinding( @Nonnull final DataOutputStream dos, @Nonnull final Binding binding )
    throws IOException
  {
    final Binding.Type bindingType = binding.getBindingType();
    dos.writeByte( bindingType.ordinal() );
    dos.writeUTF( binding.getId() );
    dos.writeUTF( binding.getQualifier() );
    final TypeMirror[] types = binding.getTypes();
    dos.writeShort( types.length );
    for ( final TypeMirror type : types )
    {
      dos.writeUTF( toFieldDescriptor( type ) );
    }
    dos.writeBoolean( binding.isEager() );
    dos.writeUTF( Binding.Type.INJECTABLE == bindingType ? "" : binding.getElement().getSimpleName().toString() );
    final DependencyDescriptor[] dependencies = binding.getDependencies();
    dos.writeShort( dependencies.length );
    for ( final DependencyDescriptor dependency : dependencies )
    {
      writeDependency( dos, dependency );
    }
  }

  @Nonnull
  private Binding readBinding( @Nonnull final DataInputStream dis, @Nonnull final TypeElement enclosingElement )
    throws IOException
  {
    final Binding.Type type = Binding.Type.values()[ dis.readByte() ];
    final String id = dis.readUTF();
    final String qualifier = dis.readUTF();
    final short typeCount = dis.readShort();
    final TypeMirror[] types = new TypeMirror[ typeCount ];
    for ( int i = 0; i < typeCount; i++ )
    {
      types[ i ] = fromFieldDescriptor( dis.readUTF() );
    }
    final boolean eager = dis.readBoolean();
    final String elementName = dis.readUTF();
    assert "".equals( elementName ) || Binding.Type.INJECTABLE != type;

    final Element element;
    final Element dependencyElement;
    if ( Binding.Type.INJECTABLE != type )
    {
      element = enclosingElement.getEnclosedElements()
        .stream()
        .filter( e -> ElementKind.METHOD == e.getKind() && e.getSimpleName().toString().equals( elementName ) )
        .findAny()
        .orElse( null );
      dependencyElement = element;
    }
    else
    {
      element = enclosingElement;
      dependencyElement = enclosingElement.getEnclosedElements()
        .stream()
        .filter( e -> ElementKind.CONSTRUCTOR == e.getKind() )
        .findAny()
        .orElse( null );
    }
    assert null != element;
    assert null != dependencyElement;

    final short dependencyCount = dis.readShort();
    final DependencyDescriptor[] dependencies = new DependencyDescriptor[ dependencyCount ];
    for ( int i = 0; i < dependencies.length; i++ )
    {
      dependencies[ i ] = readDependency( dis, dependencyElement );
    }

    return new Binding( type, id, qualifier, types, eager, element, dependencies );
  }

  private void writeDependency( @Nonnull final DataOutputStream dos, @Nonnull final DependencyDescriptor dependency )
    throws IOException
  {
    dos.writeByte( dependency.getType().ordinal() );
    writeCoordinate( dos, dependency.getCoordinate() );
    dos.writeBoolean( dependency.isOptional() );
    final Element element = dependency.getElement();
    final ElementKind kind = element.getKind();
    if ( ElementKind.METHOD == kind )
    {
      // method of @Injector annotated type
      dos.writeUTF( element.getSimpleName().toString() );
    }
    else
    {
      assert ElementKind.PARAMETER == kind;
      // parameter of @Provides method on @Fragment type or parameter of constructor on @Injectable type
      dos.writeUTF( toFieldDescriptor( element.asType() ) );
    }
    dos.writeShort( dependency.getParameterIndex() );
  }

  @Nonnull
  private DependencyDescriptor readDependency( @Nonnull final DataInputStream dis,
                                               @Nonnull final Element enclosingElement )
    throws IOException
  {
    final DependencyDescriptor.Type type = DependencyDescriptor.Type.values()[ dis.readByte() ];
    final Coordinate coordinate = readCoordinate( dis );
    final boolean optional = dis.readBoolean();
    final String elementName = dis.readUTF();
    final short parameterIndex = dis.readShort();

    final Element element;
    if ( -1 == parameterIndex )
    {
      //Must be a dependency provided by an @Injector method
      element = enclosingElement.getEnclosedElements()
        .stream()
        .filter( e -> ElementKind.METHOD == e.getKind() && e.getSimpleName().toString().equals( elementName ) )
        .findAny()
        .orElse( null );
    }
    else
    {
      element = ( (ExecutableElement) enclosingElement ).getParameters().get( parameterIndex );
    }
    assert null != element;

    return new DependencyDescriptor( type, coordinate, optional, element, parameterIndex );
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
