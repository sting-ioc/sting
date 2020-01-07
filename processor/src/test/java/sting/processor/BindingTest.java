package sting.processor;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class BindingTest
{
  @DataProvider( name = "binding" )
  public Object[][] bindings()
  {
    return new Object[][]
      {
        new Object[]{ "A",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[ 0 ],
                      true,
                      new DependencyDescriptor[ 0 ],
                      "{\"eager\":true}" },
        new Object[]{ "B",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"types\":[\"com.biz.MyService\"]}" },
        new Object[]{ "C",
                      Binding.Type.INJECTABLE,
                      "com.biz/MyQualifier",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"qualifier\":\"com.biz/MyQualifier\",\"types\":[\"com.biz.MyService\"]}" },
        new Object[]{ "D",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ),
                                        mockTypeMirror( "com.biz.OtherService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"types\":[\"com.biz.MyService\",\"com.biz.OtherService\"]}" },
        new Object[]{ "E",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              false ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"}}]}" },
        new Object[]{ "F",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "com.biz/MyQualification",
                                                              "com.biz.MyDep",
                                                              false ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"qualifier\":\"com.biz/MyQualification\",\"type\":\"com.biz.MyDep\"}}]}" },
        new Object[]{ "G",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              true ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"},\"optional\":true}]}" },
        new Object[]{ "H",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.SUPPLIER,
                                                              "",
                                                              "com.biz.Plugin",
                                                              false ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"type\":\"SUPPLIER\",\"coordinate\":{\"type\":\"com.biz.Plugin\"}}]}" },
        new Object[]{ "I",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              true ),
                                                  dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "",
                                                              "com.biz.OtherDep",
                                                              false ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"},\"optional\":true},{\"coordinate\":{\"type\":\"com.biz.OtherDep\"}}]}" },
        new Object[]{ "J",
                      Binding.Type.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Type.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              false ),
                                                  dependency( DependencyDescriptor.Type.SUPPLIER,
                                                              "Backend",
                                                              "com.biz.Plugin",
                                                              true ) },
                      "{\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"}},{\"type\":\"SUPPLIER\",\"coordinate\":{\"qualifier\":\"Backend\",\"type\":\"com.biz.Plugin\"},\"optional\":true}]}" }
      };
  }

  @Test( dataProvider = "binding" )
  public void verifyBinding( @SuppressWarnings( "unused" ) @Nonnull final String labelForDebug,
                             @Nonnull final Binding.Type bindingType,
                             @Nonnull final String qualifier,
                             @Nonnull final TypeMirror[] types,
                             final boolean eager,
                             @Nonnull final DependencyDescriptor[] dependencies,
                             @Nonnull final String expectedJson )
  {
    final Element element =
      Binding.Type.INJECTABLE == bindingType ? mock( TypeElement.class ) : mock( ExecutableElement.class );
    when( element.getKind() ).thenReturn( Binding.Type.INJECTABLE == bindingType ?
                                          ElementKind.CLASS :
                                          ElementKind.METHOD );
    final Binding binding = createBinding( bindingType, qualifier, types, eager, element, dependencies );

    assertJsonEmitsJson( binding, expectedJson );
  }

  @Nonnull
  private DependencyDescriptor dependency( @Nonnull final DependencyDescriptor.Type dependencyType,
                                           @Nonnull final String qualifier,
                                           @Nonnull final String type,
                                           final boolean optional )
  {
    return new DependencyDescriptor( dependencyType,
                                     coord( qualifier, type ),
                                     optional,
                                     mock( VariableElement.class ) );
  }

  @Nonnull
  private Coordinate coord( @Nonnull final String qualifier, @Nonnull final String type )
  {
    return new Coordinate( qualifier, mockTypeMirror( type ) );
  }

  @Nonnull
  private TypeMirror mockTypeMirror( @Nonnull final String type )
  {
    final TypeMirror mirror = mock( TypeMirror.class );
    when( mirror.toString() ).thenReturn( type );
    return mirror;
  }

  private Binding createBinding( @Nonnull final Binding.Type bindingType,
                                 @Nonnull final String qualifier,
                                 @Nonnull final TypeMirror[] types,
                                 final boolean eager,
                                 @Nonnull final Element element,
                                 @Nonnull final DependencyDescriptor[] dependencies )
  {
    final Binding binding = new Binding( bindingType, qualifier, types, eager, element, dependencies );
    assertEquals( binding.getBindingType(), bindingType );
    assertEquals( binding.getQualifier(), qualifier );
    assertEquals( binding.getTypes(), types );
    assertEquals( binding.isEager(), eager );
    assertEquals( binding.getElement(), element );
    assertEquals( binding.getDependencies(), dependencies );
    return binding;
  }

  private void assertJsonEmitsJson( @Nonnull final Binding binding, @Nonnull final String json )
  {
    final String actual = JsonUtil.formatJson( new String( writeAsJson( binding ), StandardCharsets.UTF_8 ) );
    assertEquals( actual.trim(), json );
  }

  @Nonnull
  private byte[] writeAsJson( @Nonnull final Binding binding )
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final JsonGenerator g = Json.createGenerator( baos );
    g.writeStartObject();
    binding.emitBindingJson( g );
    g.writeEnd();
    g.close();

    return baos.toByteArray();
  }
}
