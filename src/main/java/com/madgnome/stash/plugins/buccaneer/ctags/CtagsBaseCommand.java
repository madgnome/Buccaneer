package com.madgnome.stash.plugins.buccaneer.ctags;

import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.scm.BaseCommand;
import com.google.common.collect.ImmutableMap;

public class CtagsBaseCommand extends BaseCommand<Void>
{
  private static final String CTAGS_BINARY = "ctags";

  protected CtagsBaseCommand(I18nService i18nService, String command)
  {
    // TODO: Add command exit and command error handler
    super(CTAGS_BINARY, command, ImmutableMap.<String, String>of(), null, null);
  }

  @Override
  protected Void getResult()
  {
    return null;
  }
}
