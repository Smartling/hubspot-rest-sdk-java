package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostsEntityClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.common.Language;
import com.smartling.connector.hubspot.sdk.common.ListWrapper;
import com.smartling.connector.hubspot.sdk.common.PublishActionRequest;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestBlogPostEntityClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL = "http://localhost:" + PORT;
    private static final String POST_ID = "6514475261";
    private static final String FR_POST_ID = "5137751";
    private static final String PORTAL_ID = "5137750";
    private static final String BLOG_ID = "6522540979";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotBlogPostsEntityClient target;

    @Before
    public void setUpMocks()
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);
        Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.target = new HubspotRestBlogPostEntityClient(configuration, tokenProvider);
    }


    @Test
    public void shouldCallListBlogs() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/blogs")).willReturn(HttpMockUtils.aJsonResponse(blogs())));

        BlogDetails blogDetails = target.listBlogs(5, 15);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blogs"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("offset", equalTo("5"))
        );

        assertThat(blogDetails.getTotalCount()).isEqualTo(1);
        assertBlogDetail(blogDetails.getDetailList().get(0));
    }

    @Test
    public void shouldCallGetBlogById()  throws Exception
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/blogs/" + BLOG_ID)).willReturn(HttpMockUtils.aJsonResponse(blog())));

        BlogDetail blogDetail = target.getBlogById(BLOG_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blogs/" + BLOG_ID)));

        assertBlogDetail(blogDetail);
    }


    @Test
    public void shouldCallListBlogPosts() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts")).willReturn(HttpMockUtils.aJsonResponse(blogPosts())));

        target.listBlogPosts(0, 10, new BlogPostFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts"))
                .withQueryParam("limit", equalTo("10"))
                .withQueryParam("offset", equalTo("0"))
                .withQueryParam("property", equalTo(BlogPostDetail.FIELDS))

        );
    }

    @Test
    public void shouldCallGetBlogPostDetailById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID)).willReturn(HttpMockUtils.aJsonResponse(blogPost())));

        target.getBlogPostDetailById(POST_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID))
                .withQueryParam("property", equalTo(BlogPostDetail.FIELDS))
        );
    }

    @Test
    public void shouldCallGetBlogPostDetailBufferById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID + "/buffer")).willReturn(HttpMockUtils.aJsonResponse(blogPosts())));

        target.getBlogPostDetailBufferById(POST_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID + "/buffer"))
                .withQueryParam("property", equalTo(BlogPostDetail.FIELDS))
        );
    }

    @Test
    public void shouldCallGetRawBlogPostBufferById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID + "/buffer")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        target.getBlogPostBuffer(POST_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID + "/buffer")));
    }

    @Test
    public void shouldCallGetRawBlogPostById() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        target.getBlogPost(POST_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID)));
    }

    @Test
    public void shouldCallUpdateRawBlogPost() throws Exception
    {
        givenThat(put(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID)).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        target.updateBlogPost(POST_ID, blogPost());

        verify(putRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(blogPost()))
        );

    }

    @Test
    public void shouldCallUpdateRawBlogPostBuffer() throws Exception
    {
        givenThat(put(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID + "/buffer")).willReturn(HttpMockUtils.aJsonResponse("anyResponse")));

        target.updateBlogPostBuffer(blogPost(), POST_ID);

        verify(putRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID + "/buffer"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(equalTo(blogPost()))
        );

    }

    @Test
    public void shouldCallGetSupportedLanguagesUrl() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/pages/supported-languages")).willReturn(HttpMockUtils.aJsonResponse(supportedLanguages())));

        ListWrapper<Language> languages = target.getSupportedLanguages();

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/pages/supported-languages")));

        assertThat(languages.getDetailList()).hasSize(2);
        assertThat(languages.getDetailList().get(0).getTag()).isEqualTo("af");
        assertThat(languages.getDetailList().get(0).getDisplayName()).isEqualTo("Afrikaans");
        assertThat(languages.getDetailList().get(1).getTag()).isEqualTo("zh-hant");
        assertThat(languages.getDetailList().get(1).getDisplayName()).isEqualTo("Chinese (Traditional Han)");
    }


    @Test
    public void shouldCallPublishForEntityApi() throws Exception
    {
        givenThat(post(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID + "/publish-action")).willReturn(aResponse().withStatus(204)));

        target.publish(POST_ID, new PublishActionRequest());

        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/" + POST_ID + "/publish-action")));
    }

    @Test
    public void shouldCallCreateLanguageVariationUrlForEntityApi() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/blogs/v3/blog-posts/" + POST_ID)).willReturn(HttpMockUtils.aJsonResponse(blogPost())));
        givenThat(get(HttpMockUtils.path("/content/api/v2/blogs/" + BLOG_ID)).willReturn(HttpMockUtils.aJsonResponse(blog())));
        givenThat(post(HttpMockUtils.path("/cms/v3/blogs/posts")).willReturn(HttpMockUtils.aJsonResponse(blogPost())));

        target.createLanguageVariation(POST_ID, "new name", "fr");

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/blogs/v3/blog-posts/"+ POST_ID)));
        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blogs/" + BLOG_ID)));
        verify(postRequestedFor(HttpMockUtils.urlStartingWith("/cms/v3/blogs/posts")));
    }

    private String supportedLanguages()
    {
        return "{\n" +
                "  \"limit\": 0,\n" +
                "  \"objects\": [\n" +
                "    {\n" +
                "      \"displayName\": \"Afrikaans\",\n" +
                "      \"tag\": \"af\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"displayName\": \"Chinese (Traditional Han)\",\n" +
                "      \"tag\": \"zh-hant\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"offset\": 0,\n" +
                "  \"total\": 0,\n" +
                "  \"totalCount\": 0\n" +
                "}";
    }

    private String blogPosts()
    {
        return      "{\n" +
                "  \"total\": 1,\n" +
                "  \"objects\": [\n" +
                        blogPost() +
                "  ]\n" +
                "}";
    }

    private String blogPost()
    {
        return  "    {\n" +
                "      \"contentGroupId\": 6522540979,\n" +
                "      \"id\": 6514475261,\n" +
                "      \"metaDescription\": \"Aveva Demo\",\n" +
                "      \"name\": \"Inspiring Industries to Shape the Future\",\n" +
                "      \"postBody\": \"Inspiring Industries to Shape the Future post body\",\n" +
                "      \"postSummary\": \"Inspiring Industries to Shape the Future post summary\",\n" +
                "      \"publishImmediately\": false,\n" +
                "      \"slug\": \"en\",\n" +
                "      \"htmlTitle\": \"html title\",\n" +
                "      \"rssSummary\": \"rss summary\",\n" +
                "      \"featuredImageAltText\": \"alt text\"\n" +
                "    }\n";
    }

    private String blogs()
    {
        return "{" +
                "\"total\": 1,\n" +
                "\"objects\":[" + blog() + "]\n" +
                "}";
    }

    private String blog()
    {
        return "{" +
                "\"id\": " + BLOG_ID + "\n," +
                "\"name\": \"Default HubSpot Blog\"\n," +
                "\"slug\": \"blog\"\n," +
                "\"language\": \"en\"\n," +
                "\"portal_id\": " + PORTAL_ID + "\n," +
                "\"updated\": 1542204825175\n," +
                "\"translations\": {" +
                      "\"fr\":{ \"id\": " + FR_POST_ID +"}" +
                "}\n" +
                "}";
    }


    private void assertBlogDetail(BlogDetail blogDetail)
    {
        assertThat(blogDetail.getId()).isEqualTo(BLOG_ID);
        assertThat(blogDetail.getSlug()).isEqualTo("blog");
        assertThat(blogDetail.getTitle()).isEqualTo("Default HubSpot Blog");
        assertThat(blogDetail.getUpdated()).isAfter(new Date(0));
    }
}