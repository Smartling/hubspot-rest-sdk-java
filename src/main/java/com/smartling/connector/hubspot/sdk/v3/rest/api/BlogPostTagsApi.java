package com.smartling.connector.hubspot.sdk.v3.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogTagDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetails;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.blog.UpdateBlogPostTagRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostTagsApi
{
    @RequestLine("GET /cms/v3/blogs/tags?limit={limit}&after={after}&sort={sort}")
    BlogTagDetails blogPostTagList(@Param("limit") int limit,
                                   @Param("after") String after,
                                   @Param("sort") String sort);

    @RequestLine("GET /cms/v3/blogs/tags/{tagId}")
    BlogTagDetail blogPostTag(@Param("tagId") String tagId);

    @RequestLine("POST /cms/v3/blogs/tags/multi-language/create-language-variation")
    @Headers("Content-Type: application/json")
    BlogTagDetail createLanguageVariation(CloneBlogPostTagRequest cloneBlogPostTagRequest);

    @RequestLine("PATCH /cms/v3/blogs/tags/{tagId}")
    @Headers("Content-Type: application/json")
    BlogTagDetail updateBlogPostTag(@Param("tagId") String tagId, UpdateBlogPostTagRequest updateBlogPostTagRequest);

    @RequestLine("DELETE /cms/v3/blogs/tags/{tagId}")
    BlogTagDetail deleteBlogPostTag(@Param("tagId") String tagId);
}
