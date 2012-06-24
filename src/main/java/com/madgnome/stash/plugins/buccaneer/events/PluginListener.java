package com.madgnome.stash.plugins.buccaneer.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import com.atlassian.stash.content.ContentService;
import com.atlassian.stash.content.ContentTreeNode;
import com.atlassian.stash.content.DirectoryRevision;
import com.atlassian.stash.i18n.I18nService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryService;
import com.atlassian.stash.scm.ScmClientProvider;
import com.atlassian.stash.scm.git.GitScmConfig;
import com.atlassian.stash.server.ApplicationPropertiesService;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequestImpl;
import com.madgnome.stash.plugins.buccaneer.analysis.CtagsParser;
import com.madgnome.stash.plugins.buccaneer.ctags.TagFolderCommand;
import com.madgnome.stash.plugins.buccaneer.git.ClothedCloneCommand;
import com.madgnome.stash.plugins.buccaneer.model.Tags;
import com.madgnome.stash.plugins.buccaneer.model.TagsRepository;
import org.springframework.beans.factory.DisposableBean;

import java.io.*;

public class PluginListener implements DisposableBean
{
  private final EventPublisher eventPublisher;
  private final RepositoryService repositoryService;
  private final GitScmConfig gitScmConfig;
  private final ScmClientProvider clientProvider;
  private final ContentService contentService;
  private final ApplicationPropertiesService applicationPropertiesService;
  private final I18nService i18nService;
  private final TagsRepository tagsRepository;

  private final File cloneFolder;

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public PluginListener(EventPublisher eventPublisher, RepositoryService repositoryService, GitScmConfig gitScmConfig, ScmClientProvider clientProvider, ContentService contentService, ApplicationPropertiesService applicationPropertiesService, I18nService i18nService, TagsRepository tagsRepository)
  {
    this.eventPublisher = eventPublisher;
    this.repositoryService = repositoryService;
    this.gitScmConfig = gitScmConfig;
    this.clientProvider = clientProvider;
    this.contentService = contentService;
    this.applicationPropertiesService = applicationPropertiesService;
    this.i18nService = i18nService;
    this.tagsRepository = tagsRepository;

    eventPublisher.register(this);

    cloneFolder = new File(this.applicationPropertiesService.getCacheDir(), "repositories");
    cloneFolder.mkdirs();
  }

  @EventListener
  public void onPluginEnabledEvent(PluginEnabledEvent event)
  {
    final Page<? extends Repository> allRepositories = repositoryService.findAllRepositories(new PageRequestImpl(0, 5));
    for (Repository repository : allRepositories.getValues())
    {
      cloneRepository(repository);
      doCtags(repository);
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void doCtags(Repository repository)
  {
    File tagsDir = new File(applicationPropertiesService.getCacheDir(), "tags");
    File repoTagDir = new File(tagsDir, repository.getName());
    repoTagDir.mkdirs();
    File repoDir = new File(cloneFolder, repository.getName());

    final File tagsFile = new File(repoTagDir, "head.tags");
    final TagFolderCommand command = new TagFolderCommand(i18nService, repoDir.getAbsolutePath(), tagsFile.getAbsolutePath());
    command.call();

    CtagsParser ctagsParser = new CtagsParser(repoDir.getAbsolutePath());
    try
    {
      final Tags tags = ctagsParser.parseTags(new BufferedReader(new FileReader(tagsFile)));
      tagsRepository.addTags(repository, tags);
    }
    catch (IOException e)
    {
      //
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void cloneRepository(Repository repository)
  {
    File repoDir = new File(cloneFolder, repository.getName());
    if (!repoDir.exists())
    {
      repoDir.mkdirs();
      new ClothedCloneCommand(gitScmConfig, i18nService, repository, cloneFolder).call();
    }
  }

  private void explore(Repository repository, String objectId, ContentTreeNode node)
  {
    String path = node != null ? node.getPath().toString() : "";
    if (contentService.getType(repository, objectId, path) == ContentTreeNode.Type.DIRECTORY)
    {
      DirectoryRevision directoryRevision = contentService.getDirectory(repository, objectId, path, new PageRequestImpl(0, 500));
      for (ContentTreeNode childNode : directoryRevision.getChildren().getValues())
      {
        explore(repository, objectId, childNode);
      }
    }
    else
    {
      handleFile(repository, objectId, node);
    }
  }

  private void handleFile(Repository repository, String objectId, ContentTreeNode node)
  {
    // TODO: Ignore extension not handled by ctags

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    contentService.streamFile(repository, objectId, node.getPath().toString(), outputStream);

    try
    {
      final String s = outputStream.toString("UTF-8");
    }
    catch (UnsupportedEncodingException e)
    {
      //
    }
  }

  @Override
  public void destroy() throws Exception
  {
    eventPublisher.unregister(this);
  }
}
