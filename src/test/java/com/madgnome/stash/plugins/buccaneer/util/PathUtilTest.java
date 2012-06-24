package com.madgnome.stash.plugins.buccaneer.util;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PathUtilTest
{
  @Test
  public void shortenPathShouldNotCompressIfNotNecessary() throws Exception
  {
    final String path = "util/PathUtilTest.java";
    assertThat(PathUtil.shortenPath(path, path.length()), is(path));
  }

  @Test
  public void shortenPathShouldNormalizePath() throws Exception
  {
    final String path = "com/madgnome/stash/plugins/util/PathUtilTest.java";
    assertThat(PathUtil.shortenPath(path, path.length()).contains("\\"), is(false));
  }

  @Test
  public void shortenPathShouldShortenStartingFromTheStart() throws Exception
  {
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 48), is("c/madgnome/stash/plugins/util/PathUtilTest.java"));
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 41), is("c/m/stash/plugins/util/PathUtilTest.java"));
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 37), is("c/m/s/plugins/util/PathUtilTest.java"));
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 31), is("c/m/s/p/util/PathUtilTest.java"));
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 28), is("c/m/s/p/u/PathUtilTest.java"));
  }

  @Test
  public void shortenPathShouldKeepOnlyTheLastPartIfNecessary() throws Exception
  {
    assertThat(PathUtil.shortenPath("com/madgnome/stash/plugins/util/PathUtilTest.java", 18), is(".../PathUtilTest.java"));
  }
}
