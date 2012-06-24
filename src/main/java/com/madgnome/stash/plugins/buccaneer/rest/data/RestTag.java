package com.madgnome.stash.plugins.buccaneer.rest.data;

import com.madgnome.stash.plugins.buccaneer.util.PathUtil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTag
{
  @XmlElement(name = "symbol")
  private final String symbol;

  @XmlElement(name = "path")
  private final String path;

  @XmlElement(name = "shortPath")
  private final String shortPath;

  @XmlElement(name = "line")
  private final int line;

  @XmlElement(name = "excerpt")
  private final String excerpt;

  public RestTag(String symbol, String path, int line, String excerpt)
  {
    this.symbol = symbol;
    this.path = path;
    this.line = line;

    // Excerpt has a ex command form => /^ xxx $/;"
    // We only want the inner part
    this.excerpt = excerpt.substring(2, excerpt.length() - 4).trim();

    this.shortPath = PathUtil.shortenPath(path, 50);
  }
}
