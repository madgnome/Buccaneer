package com.madgnome.stash.plugins.buccaneer.git;

import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.BaseCommand;
import com.atlassian.stash.scm.git.GitCommand;
import com.atlassian.stash.scm.git.GitScmConfig;
import com.google.common.collect.ImmutableMap;

import java.util.regex.Pattern;

public abstract class GitBaseCommand<T> extends BaseCommand<T> implements GitCommand<T> {

  protected static final Pattern NOT_FOUND_PATTERN = Pattern.compile("fatal: Not a valid object name (?:([^:]*):?(.*))");

  protected GitBaseCommand(GitScmConfig config, I18nService i18nService, String command) {
    super(config.getBinary(), command, ImmutableMap.<String, String>of(), null, new GitCommandExitHandler(i18nService, null));
  }

  protected GitBaseCommand(GitScmConfig config, I18nService i18nService, Repository repository, String command) {
    super(config.getBinary(), command, ImmutableMap.<String, String>of(), config.getRepositoryDirectory(repository),
            new GitCommandExitHandler(i18nService, repository));
  }

  @Override
  protected T getResult() {
    return null;
  }

  public abstract static class AbstractBuilder {

    protected final GitScmConfig config;
    protected final I18nService i18nService;
    protected final Repository repository;

    protected AbstractBuilder(GitScmConfig config, I18nService i18nService, Repository repository) {
      this.config = config;
      this.i18nService = i18nService;
      this.repository = repository;
    }
  }
}

