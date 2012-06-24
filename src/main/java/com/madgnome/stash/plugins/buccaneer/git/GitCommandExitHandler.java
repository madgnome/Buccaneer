package com.madgnome.stash.plugins.buccaneer.git;

import com.atlassian.stash.exception.*;
import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.i18n.KeyedMessage;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.scm.DefaultCommandExitHandler;
import com.atlassian.utils.process.ProcessException;
import com.google.common.base.Throwables;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitCommandExitHandler extends DefaultCommandExitHandler
{
  public static final Pattern PATTERN_BAD_OBJECT = Pattern.compile("fatal: bad object ([0-9a-f]+)");
  public static final Pattern PATTERN_BAD_REVISION = Pattern.compile("fatal: bad revision '([^']+)'");
  public static final Pattern PATTERN_NOT_FOUND = Pattern.compile("fatal: Not a valid object name (?:([^:]*):?(.*))");
  public static final String NOT_A_TREE = "fatal: not a tree object";
  public static final String USAGE = "usage:";

  private static final Logger log = LoggerFactory.getLogger(GitCommandExitHandler.class);

  protected final Repository repository;

  public GitCommandExitHandler(I18nService i18nService, Repository repository) {
    super(i18nService);

    this.repository = repository;
  }

  @Override
  public void onCancel(@Nonnull String command, int exitCode, @Nullable String stdErr, @Nullable Throwable thrown) {
    if (StringUtils.isNotBlank(stdErr)) {
      evaluateStdErr(stdErr, command);
    }

    if (thrown != null) {
      evaluateThrowable(thrown, command, exitCode, stdErr);
    }

    super.onCancel(command, exitCode, stdErr, thrown);
  }

  protected void evaluateStdErr(String stdErr, String command) {
    if (stdErr.startsWith(USAGE)) {
      log.error("{}: The syntax used for the command is invalid. git responded by printing a usage block",
              command);
      throw new CommandFailedException(i18nService.getKeyedText("stash.scm.git.invalid.syntax",
              "Command syntax "));
    } else if (stdErr.equals(NOT_A_TREE)) {
      // TODO comment to fix checkstyle violation (@bturner feel free to kill this line when merging)
    }

    Matcher matcher = PATTERN_BAD_OBJECT.matcher(stdErr);
    if (matcher.matches()) {
      throwNoSuchChangesetException(matcher.group(1));
    }

    matcher = PATTERN_BAD_REVISION.matcher(stdErr);
    if (matcher.matches()) {
      throwNoSuchChangesetException(matcher.group(1));
    }

    matcher = PATTERN_NOT_FOUND.matcher(stdErr);
    if (matcher.matches()) {
      if (!StringUtils.isEmpty(matcher.group(2))) {
        KeyedMessage message = i18nService.getKeyedText("stash.service.repository.pathnotfound.atrevision",
                "The path \"{0}\" does not exist at revision \"{1}\"", matcher.group(2), matcher.group(1));
        throw new NoSuchPathException(message, matcher.group(2), matcher.group(1));
      } else {
        KeyedMessage message;
        if (repository != null) {
          message = i18nService.getKeyedText("stash.service.repository.objectnotfound",
                  "Object \"{1}\" does not exist in repository ''{0}''", repository.getName(), matcher.group(1));
        } else {
          message = i18nService.getKeyedText("stash.service.repository.objectnotfound.generic",
                  "Object \"{0}\" does not exist in this repository", matcher.group(1));
        }
        throw new NoSuchObjectException(message, matcher.group(1));
      }
    }
  }

  protected void evaluateThrowable(Throwable thrown, String command, int exitCode, String stdErr) {
    thrown = Throwables.getRootCause(thrown);

    //Forward anything that is already a ServiceException. The assumption here is that the original thrower has
    //already had to look after internationalisation and other considerations and has produced a well-thought-out
    //exception on their own, so we can't really add any more value here.
    if (thrown instanceof ServiceException) {
      throw (ServiceException) thrown;
    }

    String exceptionMessage = thrown.getMessage();
    if (thrown instanceof ProcessException) {
      //It's unfortunate that this has to be this way, but PluggableProcessHandler doesn't use a strongly-
      //typed exception, so string comparisons are our only recourse. The underlying motivator is that we
      //really want the exception to include any output on stderr to help debug the real failure. Just saying
      //that the return code wasn't 0 doesn't offer much insight into what went wrong.
      if (StringUtils.startsWith(thrown.getMessage(), "Non-zero exit code")) {
        StringBuilder builder = new StringBuilder("'")
                .append(command)
                .append("' exited with code ");
        builder.append(exitCode);
        if (StringUtils.isNotBlank(stdErr)) {
          builder.append(" saying: ").append(stdErr);
        }

        exceptionMessage = builder.toString();
      }
    }
    KeyedMessage message = i18nService.getKeyedText("stash.service.externalprocess.exception",
            "An error occurred while executing an external process: {0}", exceptionMessage);
    throw new ServerException(message, thrown);
  }

  @Override
  protected boolean isError(String command, int exitCode, String stdErr, Throwable thrown) {
    if (exitCode != 0 || thrown != null) {
      return true;
    }
    if (StringUtils.isNotBlank(stdErr)) {
      //git outputs a level indicating how bad the message is. To try and make sure we don't bomb out on
      //"warning" lines, look for "error" and "fatal" and "usage" prefixes.
      String[] lines = stdErr.toLowerCase().split("\\n");
      for (String line : lines) {
        if (line.startsWith("error") || line.startsWith("fatal") || line.startsWith("usage:")) {
          return true;
        }
      }
      log.debug("{} did not fail, but the following was written to stderr:\n{}", command, stdErr);
    }

    return false;
  }

  @Override
  protected void onError(String command, int exitCode, String stdErr, Throwable thrown) {
    if (StringUtils.isNotBlank(stdErr)) {
      evaluateStdErr(stdErr, command);
    }

    if (thrown != null) {
      evaluateThrowable(thrown, command, exitCode, stdErr);
    }

    //If we don't have more specific handling of our own, fall back on the default handling
    super.onError(command, exitCode, stdErr, thrown);
  }

  private void throwNoSuchChangesetException(String changesetId) {
    if (repository != null) {
      throw new NoSuchChangesetException(i18nService.getKeyedText("stash.service.repository.changesetnotfound",
              "The changeset ''{1}'' does not exist in repository ''{0}''", repository.getName(), changesetId), changesetId);
    } else {
      throw new NoSuchChangesetException(i18nService.getKeyedText("stash.service.repository.changesetnotfound.generic",
              "The changeset ''{0}'' does not exist in this repository", changesetId), changesetId);
    }
  }
}

