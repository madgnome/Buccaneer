package com.madgnome.stash.plugins.buccaneer.git;

import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.git.GitScmConfig;

import java.io.File;

// TODO: Find a better name for this class...
public final class ClothedCloneCommand extends GitBaseCommand<Void>
{
  public ClothedCloneCommand(GitScmConfig config, I18nService i18nService, Repository fromRepo, File intoDirectory) {
    super(config, i18nService, "clone");
    addArgument("--quiet");
    addArgument(config.getRepositoryDirectory(fromRepo).getAbsolutePath());
    addArgument(intoDirectory);
  }
}
