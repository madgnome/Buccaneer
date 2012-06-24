package com.madgnome.stash.plugins.buccaneer.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.stash.content.Change;
import com.atlassian.stash.event.RepositoryPushEvent;
import com.atlassian.stash.history.HistoryService;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.user.Permission;
import com.atlassian.stash.user.SecurityService;
import com.atlassian.stash.util.Operation;
import com.atlassian.stash.util.Page;
import com.atlassian.stash.util.PageRequest;
import com.atlassian.stash.util.PageRequestImpl;
import org.springframework.beans.factory.DisposableBean;

public class RepositoryListener implements DisposableBean
{
  private final EventPublisher eventPublisher;

  private final HistoryService historyService;
  private final SecurityService securityService;

  public RepositoryListener(EventPublisher eventPublisher, HistoryService historyService, SecurityService securityService)
  {
    this.eventPublisher = eventPublisher;
    this.historyService = historyService;
    this.securityService = securityService;

    eventPublisher.register(this);
  }

  @EventListener
  public void onPushEvent(RepositoryPushEvent event)
  {
    final Repository repository = event.getRepository();
    securityService.doWithPermission("changeset-indexing", Permission.REPO_READ, new Operation<Void, RuntimeException>() {

      @Override
      public Void perform() throws RuntimeException
      {
        indexRepository(repository);
        return null;
      }
    });
  }

  private void indexRepository(Repository repository)
  {
    PageRequest pageRequest = new PageRequestImpl(0, 1);
    final Page<Change> changes = historyService.getChanges(repository, "", "", pageRequest);
    for (Change change : changes.getValues())
    {

    }
  }

  @Override
  public void destroy() throws Exception
  {
    eventPublisher.unregister(this);
  }
}
