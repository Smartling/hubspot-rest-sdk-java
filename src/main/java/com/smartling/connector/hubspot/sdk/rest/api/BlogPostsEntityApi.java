package com.smartling.connector.hubspot.sdk.rest.api;

import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.CreateLanguageVariationRequest;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface BlogPostsEntityApi
{
    @RequestLine("GET /blogs/v3/blog-posts?archived={archived}&campaign={campaign}&content_group_id={content_group_id}&name__icontains={name}&slug={slug}&state={state}&offset={offset}&limit={limit}&order_by={order_by}&property=" + BlogPostDetail.FIELDS)
    ListWrapper<BlogPostDetail> listBlogPosts(@Param("archived") Boolean archived, @Param("campaign") String campaign,
                                              @Param("content_group_id") String blogId, @Param("name") String postName,
                                              @Param("slug") String slug, @Param("state") String state,
                                              @Param("offset") int offset, @Param("limit") int limit,
                                              @Param("order_by") String orderBy);

    @RequestLine("GET /blogs/v3/blog-posts/{blog_post_id}?property=" + BlogPostDetail.FIELDS)
    BlogPostDetail blogPostDetail(@Param("blog_post_id") String blogPostId);

    @RequestLine("GET /blogs/v3/blog-posts/{blog_post_id}/buffer?property=" + BlogPostDetail.FIELDS)
    BlogPostDetail blogPostDetailBuffer(@Param("blog_post_id") String blogPostId);

    @RequestLine("POST /cms/v3/blogs/posts")
    @Headers("Content-Type: application/json")
    BlogPostDetail createLanguageVariation(CreateLanguageVariationRequest createLanguageVariationRequest);

    @Headers({"x-hubspot-csrf-hubspotapi:smartling","Cookie: hubspotapi-csrf=smartling"})
    @RequestLine("GET /cos-domains/v1/domain-setup-tasks/supported-languages?portalId={portal_id}")
    ListWrapper<Language> getSupportedLanguages(@Param("portal_id") String portalId);

    @RequestLine("POST /blogs/v3/blog-posts/{blog_post_id}/publish-action")
    @Headers("Content-Type: application/json")
    void publish(@Param("blog_post_id") String blogPostId, PublishActionRequest publishActionRequest);
}
