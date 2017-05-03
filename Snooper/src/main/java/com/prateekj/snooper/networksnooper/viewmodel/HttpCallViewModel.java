package com.prateekj.snooper.networksnooper.viewmodel;

import com.prateekj.snooper.networksnooper.model.HttpCall;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static java.util.Locale.US;

public class HttpCallViewModel {

  private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy HH:mm:ss";
  private final HttpCall httpCall;

  public HttpCallViewModel(HttpCall httpCall) {
    this.httpCall = httpCall;
  }

  public String getUrl() {
    return httpCall.getUrl();
  }

  public String getMethod() {
    return httpCall.getMethod();
  }

  public int getStatusCode() {
    return httpCall.getStatusCode();
  }

  public String getStatusText() {
    return httpCall.getStatusText();
  }

  public String getTimeStamp() {
    DateFormat df = new SimpleDateFormat(TIMESTAMP_FORMAT, US);
    return df.format(httpCall.getDate());
  }

}