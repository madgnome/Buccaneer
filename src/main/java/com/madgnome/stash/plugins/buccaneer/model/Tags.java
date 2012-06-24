package com.madgnome.stash.plugins.buccaneer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tags
{
  private final Map<String, List<Tag>> tagBySymbol;

  public Tags()
  {
    tagBySymbol = new HashMap<String, List<Tag>>();
  }

  public void addTag(String symbol, Tag tag)
  {
    List<Tag> tags = tagBySymbol.get(symbol);
    if (tags == null)
    {
      tags = new ArrayList<Tag>();
      tagBySymbol.put(symbol, tags);
    }

    tags.add(tag);
  }

  public List<Tag> getTags(String symbol)
  {
    return tagBySymbol.get(symbol);
  }

}
