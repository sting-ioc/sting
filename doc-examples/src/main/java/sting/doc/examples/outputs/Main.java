package sting.doc.examples.outputs;

public class Main
{
  public static void main( String[] args )
  {
    final LibraryApplication application = LibraryApplication.create();
    final BookCatalog bookCatalog = application.getBookCatalog();

    // Lookup Hitchiker's Guide to the Galaxy
    final Book book = bookCatalog.queryByIsbn( "0434023396" );
    if ( null != book )
    {
      System.out.println( "Huzzah! The book is available" );
    }
    //0434023396
  }
}
