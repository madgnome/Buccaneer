package com.madgnome.stash.plugins.buccaneer.analysis;

import com.madgnome.stash.plugins.buccaneer.model.Tag;
import com.madgnome.stash.plugins.buccaneer.model.Tags;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class CtagsParser
{
  private final String baseDir;

  public CtagsParser(String baseDir)
  {
    this.baseDir = baseDir;
  }

  public Tags parseTags(BufferedReader reader) throws IOException
  {
    Tags tags = new Tags();

    String line;
    while ((line = reader.readLine()) != null)
    {
      if (!line.startsWith("!_TAG_"))
      {
        Tag tag = parseLine(line);
        tags.addTag(tag.getSymbol(), tag);
      }
    }

    return tags;
  }

  private Tag parseLine(String line)
  {
    int tokenNumber = 0;
    String symbol = "";
    String path = "";
    int lineNumber = -1;
    String scope = "";
    String excerpt = "";

    StringTokenizer tokenizer = new StringTokenizer(line, "\t");
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      switch (tokenNumber)
      {
        case 0:
          symbol = token;
          break;
        case 1:
          path = token.replace(baseDir + "\\", "").replace('\\', '/');
          break;
        case 2:
          excerpt = token;
          break;
        case 4:
          lineNumber = Integer.parseInt(token.substring(token.indexOf(":") + 1));
          break;
      }

      ++tokenNumber;
    }

    return new Tag(symbol, path, lineNumber, scope, excerpt);
  }

}
