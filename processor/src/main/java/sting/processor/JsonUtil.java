package sting.processor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.lang.model.element.Element;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

//TODO: Move this to proton and share with Grim
final class JsonUtil
{
  static void writeJsonResource( @Nonnull final ProcessingEnvironment processingEnv,
                                 @Nonnull final Element element,
                                 @Nonnull final String filename,
                                 @Nonnull final Consumer<JsonGenerator> action )
    throws IOException
  {
    final Map<String, Object> properties = new HashMap<>();
    properties.put( JsonGenerator.PRETTY_PRINTING, true );
    final JsonGeneratorFactory generatorFactory = Json.createGeneratorFactory( properties );

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final JsonGenerator g = generatorFactory.createGenerator( baos );
    action.accept( g );
    g.close();

    writeResource( processingEnv, filename, formatJson( baos.toString() ), element );
  }

  static void writeResource( @Nonnull final ProcessingEnvironment processingEnv,
                             @Nonnull final String filename,
                             @Nonnull final String content,
                             @Nonnull final Element element )
    throws IOException
  {
    final FileObject resource =
      processingEnv.getFiler().createResource( StandardLocation.CLASS_OUTPUT, "", filename, element );
    try ( final OutputStream outputStream = resource.openOutputStream() )
    {
      outputStream.write( content.getBytes( StandardCharsets.UTF_8 ) );
    }
    catch ( final IOException e )
    {
      resource.delete();
      throw e;
    }
  }

  /**
   * Format the json file.
   * This is horribly inefficient but it is not called very often or with big files so ... meh.
   */
  @Nonnull
  private static String formatJson( @Nonnull final String input )
  {
    return
      input
        .replaceAll( "(?m)^ {4}([^ ])", "  $1" )
        .replaceAll( "(?m)^ {8}([^ ])", "    $1" )
        .replaceAll( "(?m)^ {12}([^ ])", "      $1" )
        .replaceAll( "(?m)^ {16}([^ ])", "        $1" )
        .replaceAll( "(?m)^ {20}([^ ])", "          $1" )
        .replaceAll( "(?m)^ {24}([^ ])", "            $1" )
        .replaceAll( "(?m)^ {28}([^ ])", "              $1" )
        .replaceAll( "(?m)^ {32}([^ ])", "                $1" )
        .replaceAll( "(?m)^\n\\[\n", "[\n" )
        .replaceAll( "(?m)^\n\\{\n", "{\n" ) +
      "\n";
  }
}
