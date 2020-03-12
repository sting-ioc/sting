package sting.doc.examples.outputs;

import sting.Injector;

@Injector
public interface LibraryApplication
{
  static LibraryApplication create()
  {
    return new Sting_LibraryApplication();
  }

  BookCatalog getBookCatalog();
}
