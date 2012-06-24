package com.madgnome.stash.plugins.buccaneer.ctags;

import com.atlassian.stash.i18n.I18nService;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class TagFolderCommandTest
{
  @Test
  public void callTagFolderCommand() throws Exception
  {
    final I18nService i18nService = mock(I18nService.class);
    final TagFolderCommand command = new TagFolderCommand(i18nService, "C:\\scm\\atlassian\\buccaneer\\target\\stash\\home\\caches\\repositories", "C:\\scm\\atlassian\\buccaneer\\target\\stash\\home\\caches\\repositories\\tags");

    command.call();
  }
}
