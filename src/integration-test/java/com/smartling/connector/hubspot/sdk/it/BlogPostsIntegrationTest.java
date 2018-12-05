package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class BlogPostsIntegrationTest extends BaseIntegrationTest
{
    private static final String BASIC_POST_NAME1 = "Sample - How To Post";
    private static final String BASIC_POST_NAME2 = "Second blog post";
    private static final String BLOG_POST_ID1 = "6522541212";
    private static final String BLOG_POST_ID2 = "6696820666";
    private static final String BLOG_ID = "6522540979";

    private HubspotBlogPostClient hubspotClient;

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostClient();
    }

    @Test
    public void shouldReturnBlogs() throws Exception
    {
        BlogDetails blogDetails = hubspotClient.listBlogs(0, 1);
        assertThat(blogDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogDetail> detailList = blogDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogDetail blog = detailList.get(0);
        assertThat(blog.getId()).isNotEmpty();
        assertThat(blog.getTitle()).isNotEmpty();
    }

    @Test
    public void shouldReturnBlogPost() throws Exception
    {
        BlogPostDetail blogPost = hubspotClient.getBlogPostById(BLOG_POST_ID1);

        assertThat(blogPost).isNotNull();
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID1);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME1);
    }

    @Test
    public void shouldListBlogPostsFilterByBlogId() throws Exception
    {
        String otherBlogId = "6626419595";
        String otherBlogPostId = "6696936190";
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 5, createBlogFilter(otherBlogId), null);

        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(otherBlogPostId);
    }

    @Test
    public void shouldListBlogPostsFilterByName() throws Exception
    {
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1, createSearchFilter(BLOG_ID, BASIC_POST_NAME1, false, null, null, null), null);

        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID1);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME1);
    }

    @Test
    public void shouldListBlogPostsOrderDesc() throws Exception
    {
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1, createBlogFilter(BLOG_ID), "-publish_date");

        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID2);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME2);
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        final Configuration configuration = Configuration.build("wrong-client-id", "wrong-client-secret", "wrong-redirect-uri", "wrong-token");
        HubspotBlogPostClient hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostClient();
        hubspotClient.listBlogPosts(0, 1, createBlogFilter(BLOG_ID), null);
    }

    @Test
    public void shouldUpdateBlogPost() throws HubspotApiException
    {
        BlogPostDetail blogPostDetail = new BlogPostDetail();
        blogPostDetail.setId(BLOG_POST_ID2);
        blogPostDetail.setSlug(UUID.randomUUID().toString());

        BlogPostDetail updatedBlogPost = hubspotClient.updateBlogPost(blogPostDetail);
        assertThat(updatedBlogPost.getId()).isEqualTo(BLOG_POST_ID2);
    }

    private BlogPostFilter createBlogFilter(String blogId)
    {
        BlogPostFilter filter = new BlogPostFilter();
        filter.setBlogId(blogId);
        return filter;
    }

    private BlogPostFilter createSearchFilter(String blogId, String name, Boolean archived, String campaign, String slug, BlogPostFilter.State state)
    {
        BlogPostFilter filter = new BlogPostFilter();
        filter.setBlogId(blogId);
        filter.setPostName(name);
        filter.setArchived(archived);
        filter.setCampaign(campaign);
        filter.setSlug(slug);
        filter.setState(state);
        return filter;
    }
}