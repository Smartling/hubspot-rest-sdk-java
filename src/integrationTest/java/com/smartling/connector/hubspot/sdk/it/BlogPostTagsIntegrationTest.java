package com.smartling.connector.hubspot.sdk.it;

import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.HubspotBlogTagsClient;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetail;
import com.smartling.connector.hubspot.sdk.blog.BlogTagDetails;
import com.smartling.connector.hubspot.sdk.blog.CloneBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.blog.UpdateBlogPostTagRequest;
import com.smartling.connector.hubspot.sdk.rest.Configuration;
import com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.smartling.connector.hubspot.sdk.rest.HubspotRestClientManager.createTokenProvider;
import static org.fest.assertions.api.Assertions.assertThat;

public class BlogPostTagsIntegrationTest extends BaseIntegrationTest
{
    private static final String TAG_ID = "180766564737";

    private HubspotBlogTagsClient hubspotClient;

    private final List<String> tagsToDelete = new ArrayList<>();

    @Before
    public void init()
    {
        final Configuration configuration = Configuration.build(clientId, clientSecret, redirectUri, refreshToken);
        hubspotClient = new HubspotRestClientManager(configuration, createTokenProvider(configuration)).getBlogTagsClient();
    }

    @After
    public void deleteTestBlogPosts()
    {
        for (String tagId : tagsToDelete)
        {
            try
            {
                hubspotClient.deleteBlogPostTag(tagId);
            }
            catch (HubspotApiException e)
            {
                System.err.printf("Fail to clean up blog post tags '%1$s', cause '%2$s'", tagId, e);
            }
        }
    }

    @Test
    public void shouldReturnAllTags() throws Exception
    {
        BlogTagDetails blogDetails = hubspotClient.listBlogTags(0, null, null);
        assertThat(blogDetails).isNotNull();
        assertThat(blogDetails.getDetailList()).isNotEmpty();
    }

    @Test
    public void shouldCreateLanguageVariationForBlogPostTag() throws Exception
    {
        BlogTagDetail variationDetails = hubspotClient.createLanguageVariation(new CloneBlogPostTagRequest(TAG_ID, "variationName", "de-de", "en-US"));
        assertThat(variationDetails).isNotNull();
        tagsToDelete.add(variationDetails.getId());
        assertThat(variationDetails.getName()).isEqualTo("variationName");
        assertThat(variationDetails.getLanguage()).isEqualTo("de-de");
    }

    @Test
    public void shouldUpdateBlogPostTag() throws Exception
    {
        BlogTagDetail blogTagDetail = hubspotClient.blogPostTag(TAG_ID);
        String dynamicName = String.valueOf(System.currentTimeMillis());
        BlogTagDetail updated = hubspotClient.updateBlogPostTag(blogTagDetail.getId(), new UpdateBlogPostTagRequest(blogTagDetail.getId(), dynamicName, "en-US", "0"));
        assertThat(updated.getName()).isEqualTo(dynamicName);
    }
}
