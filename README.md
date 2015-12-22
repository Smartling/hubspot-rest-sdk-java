# hubspot-rest-sdk-java
Java API for HubSpot REST API

Provides Java API to Hubspos COS Page Publishing API and Authorization.

Some methods use JSON-POJO mapping: 

- list pages
- refresh access token
- clone page

Another methods load and pass page as JSON transparently (SDK user is responcible for mapping, processing of JSON):

- get page
- update page

## Build of SDK

` ./gradlew.sh clean build`

## Run of integration tests

` ./gradlew.sh clean integrationTest -Dhubspot.refreshToken=<refreshToken> -Dhubspot.clientId=<clientIdOfHubspotApplication> -Dhubspot.basicFormId=<DefaultFormId> -Dhubspot.notLivePageCampaignId=<notLivePageCampaignId> -Dhubspot.basicPageId=<basicPageId> -Dhubspot.archivedPageId=<archivedPageId> -Dhubspot.notLivePageId=<notLivePageId>`


## Using the client library

### Creating instance of Client

`HubspotClient client = hubspotClient = new HubspotRestClient(<clientIdOfHubspotApplication>, <refreshToken>);`

### Listing pages with limited number of mapped fields

Requesting 10 pages, starting from the first one (i.e. with no offset):

`PageDetails pageDetails = hubspotClient.listPages(0, 10);`

### Loading whole page as JSON

`String pageAsJson = hubspotClient.getPageById(<pageID>);`

### Update page

`String updatedPage = hubspotClient.updatePage(pageSnippetAsJson);`

### Refresh access token

`RefreshTokenData tokenData = hubspotClient.refreshToken();`

