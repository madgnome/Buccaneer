package com.madgnome.stash.plugins.buccaneer.model;

public class Tag
{
  private final String symbol;
  private final String path;
  private final int lineNumber;
  private final String scope;
  private final String excerpt;

  public Tag(String symbol, String path, int lineNumber, String scope, String excerpt)
  {
    this.symbol = symbol;
    this.path = path;
    this.lineNumber = lineNumber;
    this.scope = scope;
    this.excerpt = excerpt;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getPath()
  {
    return path;
  }

  public int getLineNumber()
  {
    return lineNumber;
  }

  public String getScope()
  {
    return scope;
  }

  public String getExcerpt()
  {
    return excerpt;
  }
}
