package com.smartling.connector.hubspot.sdk.rest;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogPostClient;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.blog.BlogDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogPostDetails;
import com.smartling.connector.hubspot.sdk.blog.BlogPostFilter;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.fest.assertions.api.Assertions.assertThat;

public class HubspotRestBlogPostClientTest
{
    private static final int PORT = 10000 + new Random().nextInt(9999);

    private static final String BASE_URL      = "http://localhost:" + PORT;
    private static final String POST_ID = "6514475261";

    @Rule
    public final WireMockRule wireMockRule = new WireMockRule(PORT);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private TokenProvider tokenProvider;
    private String originalToken;
    private HubspotBlogPostClient hubspotClient;

    @Before
    public void setUpMocks() throws Exception
    {
        this.originalToken = RandomStringUtils.randomAlphanumeric(36);

        final Configuration configuration = Configuration.build(BASE_URL, null, null, null, null);
        final RefreshTokenData refreshTokenData = new RefreshTokenData();
        refreshTokenData.setAccessToken(originalToken);
        tokenProvider = () -> refreshTokenData;
        this.hubspotClient = new HubspotRestBlogPostClient(configuration, tokenProvider);
    }

    @Test
    public void shouldListBlogs() throws IOException, URISyntaxException, HubspotApiException
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/blogs")).willReturn(HttpMockUtils.aJsonResponse(loadResource("blogs.json"))));

        BlogDetails blogDetails = hubspotClient.listBlogs(5, 15);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blogs"))
                .withQueryParam("limit", equalTo("15"))
                .withQueryParam("offset", equalTo("5"))
        );

        assertThat(blogDetails.getTotalCount()).isEqualTo(1);
        assertBlogDetail(blogDetails.getDetailList().get(0));
    }

    private void assertBlogDetail(BlogDetail blogDetail)
    {
        assertThat(blogDetail.getId()).isEqualTo("6522540979");
        assertThat(blogDetail.getSlug()).isEqualTo("blog");
        assertThat(blogDetail.getTitle()).isEqualTo("Default HubSpot Blog");
        assertThat(blogDetail.getUpdated()).isAfter(new Date(0));
    }

    @Test
    public void shouldCallGetBlogPostById() throws HubspotApiException, IOException, URISyntaxException
    {

        givenThat(get(HttpMockUtils.path("/content/api/v2/blog-posts/" + POST_ID)).willReturn(HttpMockUtils.aJsonResponse(loadResource("blog_post.json"))));

        hubspotClient.getBlogPostById(POST_ID);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blog-posts/" + POST_ID)));

    }

    @Test
    public void shouldCallListBlogPosts() throws Exception
    {
        givenThat(get(HttpMockUtils.path("/content/api/v2/blog-posts")).willReturn(HttpMockUtils.aJsonResponse(loadResource("blog_posts.json"))));

        hubspotClient.listBlogPosts(5, 15, new BlogPostFilter(), null);

        verify(getRequestedFor(HttpMockUtils.urlStartingWith("/content/api/v2/blog-posts"))
                        .withQueryParam("limit", equalTo("15"))
                        .withQueryParam("offset", equalTo("5"))
        );
    }

    @Test
    public void shouldCallCreateBlogPost() throws Exception
    {
        withPostHttpResponseData("/content/api/v2/blog-posts", loadResource("blog_post.json"));

        String json = loadResource("translated_blog_post.json");
        BlogPostDetail blogPostDetail = getTranslatedBlogPost(json);

        BlogPostDetail blogPost = hubspotClient.createBlogPost(blogPostDetail);

        ValueMatchingStrategy valueMatchingStrategy = new ValueMatchingStrategy();
        valueMatchingStrategy.setEqualToJson(json);
        verify(postRequestedFor(HttpMockUtils.path("/content/api/v2/blog-posts"))
                .withRequestBody(valueMatchingStrategy));

        assertThat(blogPost.getId()).isEqualTo(POST_ID);
    }

    @Test
    public void shouldCallUpdateBlogPost() throws Exception
    {
        withPutHttpResponseData("/content/api/v2/blog-posts/1", loadResource("blog_post.json"));

        String json = loadResource("translated_blog_post.json");
        BlogPostDetail blogPostDetail = getTranslatedBlogPost(json);
        blogPostDetail.setId("1");

        BlogPostDetail blogPost = hubspotClient.updateBlogPost(blogPostDetail);

        ValueMatchingStrategy valueMatchingStrategy = new ValueMatchingStrategy();
        valueMatchingStrategy.setEqualToJson(json);
        verify(putRequestedFor(HttpMockUtils.path("/content/api/v2/blog-posts/1"))
                .withRequestBody(valueMatchingStrategy));

        assertThat(blogPost.getId()).isEqualTo(POST_ID);
    }

    @Test
    public void shouldDeserializeFields() throws Exception
    {
        givenThat(get(HttpMockUtils.urlStartingWith("/content/api/v2/blog-posts")).willReturn(HttpMockUtils.aJsonResponse(loadResource("blog_posts.json"))));

        BlogPostDetails blogPostDetails = hubspotClient.listBlogPosts(5, 15, new BlogPostFilter(), null);

        assertThat(blogPostDetails).isNotNull();
        assertThat(blogPostDetails.getTotalCount()).isEqualTo(8);

        List<BlogPostDetail> detailList = blogPostDetails.getDetailList();
        assertThat(detailList).isNotEmpty();

        assertPostDetail(detailList.get(0));
    }

    private BlogPostDetail getTranslatedBlogPost(String json)
    {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        return gson.fromJson(json, BlogPostDetail.class);
    }

    private void withPostHttpResponseData(String url, String data)
    {
        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withBody(data)));
    }

    private void withPutHttpResponseData(String url, String data)
    {
        stubFor(put(urlEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody(data)));
    }

    private String loadResource(String name) throws IOException, URISyntaxException
    {
        URI uri = HubspotRestBlogPostClientTest.class.getClassLoader().getResource(name).toURI();
        return new String(Files.readAllBytes(Paths.get(uri)), Charset.forName("utf-8"));
    }

    private void assertPostDetail(final BlogPostDetail blogPostDetail)
    {
        assertThat(blogPostDetail.getName()).isEqualTo("My Blog Post - ES2");
        assertThat(blogPostDetail.getMetaDescription()).isEqualTo("meta");
        assertThat(blogPostDetail.getPostBody()).startsWith("<p><span");
        assertThat(blogPostDetail.getPostSummary()).startsWith("<p><span");
        assertThat(blogPostDetail.isPublishImmediately()).isTrue();
        assertThat(blogPostDetail.getSlug()).isEqualTo("tb-es/-temporary-slug-d69558bb-941d-4d3e-8ba9-f5e39d97ab12");
        assertThat(blogPostDetail.getId()).isEqualTo(POST_ID);
        assertThat(blogPostDetail.getWidgets()).containsKey("blog_comments");
        assertThat(blogPostDetail.getWidgets().get("blog_comments")).hasSize(6);
    }
}
