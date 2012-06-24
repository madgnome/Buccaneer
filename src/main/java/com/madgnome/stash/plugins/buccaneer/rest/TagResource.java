package com.madgnome.stash.plugins.buccaneer.rest;

import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.repository.RepositoryService;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.madgnome.stash.plugins.buccaneer.model.Tag;
import com.madgnome.stash.plugins.buccaneer.model.Tags;
import com.madgnome.stash.plugins.buccaneer.model.TagsRepository;
import com.madgnome.stash.plugins.buccaneer.rest.data.RestTag;
import com.madgnome.stash.plugins.buccaneer.rest.data.RestTags;
import com.sun.jersey.spi.resource.Singleton;

import javax.annotation.Nullable;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/tags")
@Singleton
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
public class TagResource
{
  private final TagsRepository tagsRepository;
  private final RepositoryService repositoryService;

  public TagResource(TagsRepository tagsRepository, RepositoryService repositoryService)
  {
    this.tagsRepository = tagsRepository;
    this.repositoryService = repositoryService;
  }

  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public Response getTag(@QueryParam("projectKey") String projectKey, @QueryParam("slug") String slug, @QueryParam("path") String filePath, @QueryParam("type") String type)
  {
    final Repository repository = repositoryService.findRepositoryBySlug("PROJECT_1", "rep_1");

    final Tags tags = tagsRepository.getTags(repository);
    final List<Tag> symbolTags = tags.getTags(type);
    final List<RestTag> restSymbolTags = Lists.transform(symbolTags, new Function<Tag, RestTag>()
    {
      @Override
      public RestTag apply(@Nullable Tag tag)
      {
        return new RestTag(tag.getSymbol(), tag.getPath(), tag.getLineNumber(), tag.getExcerpt());
      }
    });

    final RestTags restTags = new RestTags(type, restSymbolTags);

    return Response.ok(restTags).build ();
  }
}
