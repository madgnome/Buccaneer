package com.madgnome.stash.plugins.buccaneer.rest.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "tags")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestTags
{
  @XmlElement(name = "type")
  private final String type;

  @XmlElement(name = "tags")
  final List<RestTag> tags;

  public RestTags(String type, List<RestTag> tags)
  {
    this.type = type;
    this.tags = tags;
  }
}
