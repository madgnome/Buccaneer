package com.madgnome.stash.plugins.buccaneer.ctags;

import com.atlassian.stash.i18n.I18nService;

public class TagFolderCommand extends CtagsBaseCommand
{
  public TagFolderCommand(I18nService i18nService, String sourceFolder, String toFile)
  {
    super(i18nService, "-R");
    addArgument("-f " + toFile);
    addArgument("--fields=-anf+iKnS");
    addArgument(sourceFolder);
  }
}
