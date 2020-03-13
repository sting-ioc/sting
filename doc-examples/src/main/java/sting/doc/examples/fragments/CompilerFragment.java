package sting.doc.examples.fragments;

import sting.Fragment;

@Fragment( includes = { FileSystemService.class, ParserFragment.class } )
public interface CompilerFragment
{
  default Compiler getCompiler( FileSystemService fileSystemService, Parser parser )
  {
    return
      new Compiler.Builder()
        .setFileSystemService( fileSystemService )
        .setParser( parser )
        .setDiagnosticsReporter( new StdoutDiagnosticsReporter() )
        .build();
  }
  //DOC ELIDE START
  //DOC ELIDE END
}
