package com.madgnome.stash.plugins.buccaneer.analysis;

import com.madgnome.stash.plugins.buccaneer.model.Tag;
import com.madgnome.stash.plugins.buccaneer.model.Tags;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class CtagsParserTest
{
  @Test
  public void parseShouldReturnTag() throws Exception
  {
    InputStream in = this.getClass().getResourceAsStream("/analysis/tags");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    CtagsParser parser = new CtagsParser("");
    Tags tags = parser.parseTags(reader);

    final List<Tag> symbolTags = tags.getTags("DefinitionResource");
    assertThat(symbolTags, is(not(nullValue())));
    assertThat(symbolTags.size(), is(1));
  }

  @Test
  public void parseShouldReturnTags() throws Exception
  {
    InputStream in = this.getClass().getResourceAsStream("/analysis/tags");
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

    CtagsParser parser = new CtagsParser("");
    Tags tags = parser.parseTags(reader);

    final List<Tag> symbolTags = tags.getTags("FileDefinitionResourceModel");
    assertThat(symbolTags, is(not(nullValue())));
    assertThat(symbolTags.size(), is(3));
  }
}
