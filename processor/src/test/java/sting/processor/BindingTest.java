package sting.processor;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.realityforge.proton.JsonUtil;
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
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[ 0 ],
                      true,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"A\",\"eager\":true}" },
        new Object[]{ "B",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"B\",\"types\":[\"com.biz.MyService\"]}" },
        new Object[]{ "C",
                      Binding.Kind.INJECTABLE,
                      "com.biz/MyQualifier",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"C\",\"qualifier\":\"com.biz/MyQualifier\",\"types\":[\"com.biz.MyService\"]}" },
        new Object[]{ "D",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ),
                                        mockTypeMirror( "com.biz.OtherService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"D\",\"types\":[\"com.biz.MyService\",\"com.biz.OtherService\"]}" },
        new Object[]{ "E",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              false ) },
                      "{\"id\":\"E\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"}}]}" },
        new Object[]{ "F",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "com.biz/MyQualification",
                                                              "com.biz.MyDep",
                                                              false ) },
                      "{\"id\":\"F\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"qualifier\":\"com.biz/MyQualification\",\"type\":\"com.biz.MyDep\"}}]}" },
        new Object[]{ "G",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              true ) },
                      "{\"id\":\"G\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"},\"optional\":true}]}" },
        new Object[]{ "H",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.SUPPLIER,
                                                              "",
                                                              "com.biz.Plugin",
                                                              false ) },
                      "{\"id\":\"H\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"type\":\"SUPPLIER\",\"coordinate\":{\"type\":\"com.biz.Plugin\"}}]}" },
        new Object[]{ "I",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              true ),
                                                  dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "",
                                                              "com.biz.OtherDep",
                                                              false ) },
                      "{\"id\":\"I\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"},\"optional\":true},{\"coordinate\":{\"type\":\"com.biz.OtherDep\"}}]}" },
        new Object[]{ "J",
                      Binding.Kind.INJECTABLE,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[]{ dependency( DependencyDescriptor.Kind.INSTANCE,
                                                              "",
                                                              "com.biz.MyDep",
                                                              false ),
                                                  dependency( DependencyDescriptor.Kind.SUPPLIER,
                                                              "Backend",
                                                              "com.biz.Plugin",
                                                              true ) },
                      "{\"id\":\"J\",\"types\":[\"com.biz.MyService\"],\"dependencies\":[{\"coordinate\":{\"type\":\"com.biz.MyDep\"}},{\"type\":\"SUPPLIER\",\"coordinate\":{\"qualifier\":\"Backend\",\"type\":\"com.biz.Plugin\"},\"optional\":true}]}" },
        new Object[]{ "K",
                      Binding.Kind.PROVIDES,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"K\",\"providesMethod\":\"myProviderMethod\",\"types\":[\"com.biz.MyService\"]}" },
        new Object[]{ "L",
                      Binding.Kind.NULLABLE_PROVIDES,
                      "",
                      new TypeMirror[]{ mockTypeMirror( "com.biz.MyService" ) },
                      false,
                      new DependencyDescriptor[ 0 ],
                      "{\"id\":\"L\",\"providesMethod\":\"myProviderMethod\",\"nullable\":true,\"types\":[\"com.biz.MyService\"]}" }
      };
  }

  @Test( dataProvider = "binding" )
  public void verifyBinding( @Nonnull final String id,
                             @Nonnull final Binding.Kind kind,
                             @Nonnull final String qualifier,
                             @Nonnull final TypeMirror[] types,
                             final boolean eager,
                             @Nonnull final DependencyDescriptor[] dependencies,
                             @Nonnull final String expectedJson )
  {
    final ExecutableElement element = mock( ExecutableElement.class );
    if ( Binding.Kind.INJECTABLE != kind )
    {
      final Name name = mock( Name.class );
      when( name.toString() ).thenReturn( "myProviderMethod" );
      when( element.getSimpleName() ).thenReturn( name );
    }
    when( element.getKind() )
      .thenReturn( Binding.Kind.INJECTABLE == kind ? ElementKind.CONSTRUCTOR : ElementKind.METHOD );
    final Binding binding = createBinding( id, kind, qualifier, types, eager, element, dependencies );

    assertJsonEmitsJson( binding, expectedJson );
  }

  @Nonnull
  private DependencyDescriptor dependency( @Nonnull final DependencyDescriptor.Kind dependencyType,
                                           @Nonnull final String qualifier,
                                           @Nonnull final String type,
                                           final boolean optional )
  {
    return new DependencyDescriptor( dependencyType,
                                     coord( qualifier, type ),
                                     optional,
                                     mock( VariableElement.class ),
                                     1 );
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

  @Nonnull
  private Binding createBinding( @Nonnull final String id,
                                 @Nonnull final Binding.Kind kind,
                                 @Nonnull final String qualifier,
                                 @Nonnull final TypeMirror[] types,
                                 final boolean eager,
                                 @Nonnull final ExecutableElement element,
                                 @Nonnull final DependencyDescriptor[] dependencies )
  {
    final Binding binding = new Binding( kind, id, qualifier, types, eager, element, dependencies );
    assertEquals( binding.getKind(), kind );
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
    binding.write( g );
    g.writeEnd();
    g.close();

    return baos.toByteArray();
  }
}
