package sting.doc.examples.fragments;

import sting.Fragment;

@Fragment
public interface ParserFragment
{
  default Parser createParser()
  {
    return new Parser();
  }
}
