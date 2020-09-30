package com.smartling.connector.hubspot.sdk.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostsClient;
import com.smartling.connector.hubspot.sdk.ResultInfo;
import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class BlogPostsIntegrationTest extends BaseIntegrationTest
{
    private static final String BASIC_POST_NAME1 = "Demo Blog Post";
    private static final String BLOG_POST_ID1 = "6728540881";
    private static final String BLOG_POST_ID2 = "6741114424";
    private static final String BLOG_ID = "6724977225";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(SerializationFeature.INDENT_OUTPUT, true)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);

    private HubspotBlogPostsClient hubspotClient;

    private List<String>  blogPostsToDelete = new ArrayList<>();

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostsClient();
    }

    @After
    public void deleteTestBlogPosts()
    {
        for (String blogPostId : blogPostsToDelete)
        {
            try
            {
                hubspotClient.deleteBlogPost(blogPostId);
            }
            catch (HubspotApiException e)
            {
                System.err.printf("Fail to clean up blog post '%1$s', cause '%2$s'", blogPostId, e);
            }
        }
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
    public void shouldReturnBlog() throws Exception
    {
        BlogDetail blog = hubspotClient.getBlogById(BLOG_ID);

        assertThat(blog).isNotNull();
        assertThat(blog.getSlug()).isEqualTo("blog");
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
    public void shouldReturnRawBlogPost() throws Exception
    {
        String blogPostJson = hubspotClient.getBlogPost(BLOG_POST_ID1);
        BlogPostDetail blogPost = OBJECT_MAPPER.readValue(blogPostJson, BlogPostDetail.class);

        assertThat(blogPost).isNotNull();
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID1);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME1);
    }

    @Test
    public void shouldListBlogPostsFilterByBlogId() throws Exception
    {
        String otherBlogId = "6724977255";
        String otherBlogPostId = "6729041952";
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
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1, createSearchFilter(BLOG_ID, "demo", false, null, null, null), null);

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
        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(0, 1, createBlogFilter(BLOG_ID), "publish_date");

        assertThat(blogPostDetails).overridingErrorMessage("Page details object should not be null").isNotNull();
        assertThat(blogPostDetails.getTotalCount()).overridingErrorMessage("Total count should not be positive").isPositive();

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).overridingErrorMessage("Page details should not be empty and have particular size").hasSize(1);

        BlogPostDetail blogPost = detailList.get(0);
        assertThat(blogPost.getId()).isEqualTo(BLOG_POST_ID1);
        assertThat(blogPost.getName()).isEqualTo(BASIC_POST_NAME1);
    }

    @Test(expected = HubspotApiException.class)
    public void shouldThrowExceptionIfAuthorizationFailed() throws HubspotApiException
    {
        final Configuration configuration = Configuration.build("wrong-client-id", "wrong-client-secret", "wrong-redirect-uri", "wrong-token");
        HubspotBlogPostsClient hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogPostsClient();
        hubspotClient.listBlogPosts(0, 1, createBlogFilter(BLOG_ID), null);
    }

    @Test
    public void shouldUpdateBlogPost() throws HubspotApiException
    {
        BlogPostDetail blogPostDetail = new BlogPostDetail();
        blogPostDetail.setId(BLOG_POST_ID2);
        blogPostDetail.setMetaDescription("new meta");

        BlogPostDetail updatedBlogPost = hubspotClient.updateBlogPost(blogPostDetail);
        assertThat(updatedBlogPost.getId()).isEqualTo(BLOG_POST_ID2);
    }

    @Test
    public void shouldUpdateRawBlogPost() throws HubspotApiException, IOException
    {
        BlogPostDetail blogPostDetail = new BlogPostDetail();
        blogPostDetail.setId(BLOG_POST_ID2);
        blogPostDetail.setMetaDescription("new meta");

        String blogPostJson = OBJECT_MAPPER.writeValueAsString(blogPostDetail);

        String blogPostJsonUpdated = hubspotClient.updateBlogPost(BLOG_POST_ID2, blogPostJson);
        BlogPostDetail updatedBlogPost = OBJECT_MAPPER.readValue(blogPostJsonUpdated, BlogPostDetail.class);
        assertThat(updatedBlogPost.getId()).isEqualTo(BLOG_POST_ID2);
        assertThat(updatedBlogPost.getMetaDescription()).isEqualTo("new meta");
    }

    @Test(expected = HubspotApiException.class)
    public void shouldFailToUpdateBecauseOfConflict() throws HubspotApiException
    {
        String otherBlogPostId = "6729041952";

        BlogPostDetail blogPostById = hubspotClient.getBlogPostById(BLOG_POST_ID2);

        blogPostById.setId(otherBlogPostId);

        hubspotClient.updateBlogPost(blogPostById);
    }

    @Test
    public void shouldCloneBlogPost() throws HubspotApiException {
        String blogPostId = BLOG_ID;
        String name = "Cloned";
        BlogPostDetail blogPostDetail = hubspotClient.getBlogPostById(blogPostId);

        BlogPostDetail blogPostCloneDetail = hubspotClient.cloneBlogPost(blogPostId, name);
        blogPostsToDelete.add(blogPostCloneDetail.getId());

        assertThat(blogPostCloneDetail.getName()).isEqualTo("Cloned");
        assertThat(blogPostDetail.getContentGroupId()).isEqualTo(blogPostCloneDetail.getContentGroupId());
    }

    @Test
    public void shouldDeleteBlogPost() throws HubspotApiException {
        BlogPostDetail blogPostCloneDetail = hubspotClient.cloneBlogPost(BLOG_ID, "Cloned");
        blogPostsToDelete.add(blogPostCloneDetail.getId());

        ResultInfo info = hubspotClient.deleteBlogPost(blogPostCloneDetail.getId());
        assertThat(info.isSucceeded()).isTrue();

        blogPostsToDelete.remove(blogPostCloneDetail.getId());
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