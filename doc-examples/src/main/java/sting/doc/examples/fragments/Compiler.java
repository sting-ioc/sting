package sting.doc.examples.fragments;

public class Compiler
{
  public static class Builder
  {
    Builder setFileSystemService( FileSystemService fileSystemService )
    {
      return this;
    }

    Builder setDiagnosticsReporter( DiagnosticsReporter fileSystemService )
    {
      return this;
    }

    Builder setParser( Parser parser )
    {
      return this;
    }

    Compiler build()
    {
      return new Compiler();
    }
  }
}
