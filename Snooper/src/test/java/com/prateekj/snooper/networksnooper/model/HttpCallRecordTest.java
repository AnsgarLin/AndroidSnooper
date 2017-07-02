package com.prateekj.snooper.networksnooper.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HttpCallRecordTest {
  private HttpCallRecord httpCallRecord;

  @Before
  public void setUp() throws Exception {
    final String url = "https://ajax.googleapis.com/ajax/services/search/web?v=1.0";
    final String responseBody = "responseBody";
    final String requestBody = "requestBody";
    httpCallRecord = HttpCallRecord.from(new HttpCall.Builder()
      .withUrl(url)
      .withMethod("POST")
      .withPayload(requestBody)
      .withResponseBody(responseBody)
      .withStatusCode(200)
      .withStatusText("OK")
      .withRequestHeaders(getRequestHeaders())
      .withResponseHeaders(getResponseHeaders())
      .build());
    assertNotNull(httpCallRecord.getDate());
  }

  @Test
  public void shouldReturnRequestHeaderByGivenName() throws Exception {
    HttpHeader requestHeader = httpCallRecord.getRequestHeader("User-Agent");
    assertThat(requestHeader.getValues().get(0).getValue(), is("Android Browser"));
  }

  @Test
  public void shouldReturnRequestHeaderByGivenNameByIgnoringCase() throws Exception {
    HttpHeader requestHeader = httpCallRecord.getRequestHeader("USER-AGENT");
    assertThat(requestHeader.getValues().get(0).getValue(), is("Android Browser"));
  }

  @Test
  public void shouldReturnNullWhenHeaderByGivenNameNotFound() throws Exception {
    HttpHeader requestHeader = httpCallRecord.getRequestHeader("Invalid Name");
    assertNull(requestHeader);
  }

  @Test
  public void shouldReturnResponseHeaderByGivenName() throws Exception {
    HttpHeader responseHeader = httpCallRecord.getResponseHeader("date");
    assertThat(responseHeader.getValues().get(0).getValue(), is("Thu, 02 Mar 2017 13:03:11 GMT"));
  }

  @Test
  public void shouldReturnResponseHeaderByGivenNameByIgnoringCase() throws Exception {
    HttpHeader responseHeader = httpCallRecord.getResponseHeader("DATE");
    assertThat(responseHeader.getValues().get(0).getValue(), is("Thu, 02 Mar 2017 13:03:11 GMT"));
  }

  @Test
  public void shouldReturnNullWhenResponseHeaderByGivenNameNotFound() throws Exception {
    HttpHeader responseHeader = httpCallRecord.getResponseHeader("Invalid Name");
    assertNull(responseHeader);
  }

  private Map<String, List<String>> getResponseHeaders() {
    Map<String, List<String>> headers = new HashMap<>();
    List<String> xssProtectionHeader = Arrays.asList("1", "mode=block");
    List<String> dateHeader = singletonList("Thu, 02 Mar 2017 13:03:11 GMT");
    headers.put("x-xss-protection", xssProtectionHeader);
    headers.put("date", dateHeader);
    return headers;
  }

  private Map<String, List<String>> getRequestHeaders() {
    Map<String, List<String>> headers = new HashMap<>();
    List<String> cacheControlHeader = Arrays.asList("public", "max-age=86400", "no-transform");
    List<String> userAgentHeader = singletonList("Android Browser");
    headers.put("User-Agent", userAgentHeader);
    headers.put("cache-control", cacheControlHeader);
    return headers;
  }


}