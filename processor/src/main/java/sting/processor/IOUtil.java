package sting.processor;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.tools.FileObject;

//TODO: Move this to proton
public final class IOUtil
{
  private IOUtil()
  {
  }

  public static byte[] readFully( @Nonnull final FileObject fileObject )
    throws IOException
  {
    try ( final InputStream inputStream = fileObject.openInputStream() )
    {
      int offset = 0;
      int remaining = inputStream.available();
      final byte[] data = new byte[ remaining ];
      int count;
      while ( 0 < ( count = inputStream.read( data, offset, remaining ) ) )
      {
        remaining -= count;
        offset += count;
      }
      if ( 0 != remaining )
      {
        throw new IOException( "Unable to read resource fully: " + fileObject.getName() );
      }
      return data;
    }
  }
}
