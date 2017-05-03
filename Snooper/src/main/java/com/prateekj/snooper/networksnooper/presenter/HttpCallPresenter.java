package com.prateekj.snooper.networksnooper.presenter;

import com.prateekj.snooper.networksnooper.activity.HttpCallTab;
import com.prateekj.snooper.formatter.ResponseFormatter;
import com.prateekj.snooper.formatter.ResponseFormatterFactory;
import com.prateekj.snooper.infra.BackgroundTask;
import com.prateekj.snooper.infra.BackgroundTaskExecutor;
import com.prateekj.snooper.networksnooper.model.HttpCall;
import com.prateekj.snooper.networksnooper.model.HttpHeader;
import com.prateekj.snooper.networksnooper.repo.SnooperRepo;
import com.prateekj.snooper.utils.FileUtil;
import com.prateekj.snooper.networksnooper.views.HttpCallView;

import org.apache.commons.lang3.StringUtils;

import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.REQUEST_MODE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallActivity.RESPONSE_MODE;
import static com.prateekj.snooper.networksnooper.activity.HttpCallTab.RESPONSE;
import static com.prateekj.snooper.networksnooper.model.HttpHeader.CONTENT_TYPE;

public class HttpCallPresenter {

  private final FileUtil fileUtil;
  private final BackgroundTaskExecutor executor;
  private HttpCall httpCall;
  private HttpCallView view;
  private ResponseFormatterFactory formatterFactory;
  private boolean requestViewLoaded;
  private boolean responseViewLoaded;

  public HttpCallPresenter(int callId, SnooperRepo snooperRepo, HttpCallView view, ResponseFormatterFactory formatterFactory, FileUtil fileUtil, BackgroundTaskExecutor executor) {
    this.httpCall = snooperRepo.findById(callId);
    this.view = view;
    this.formatterFactory = formatterFactory;
    this.fileUtil = fileUtil;
    this.executor = executor;
  }

  public void copyHttpCallBody(int selectedItem) {
    String text = getTextToCopy(selectedItem);
    view.copyToClipboard(text);
  }

  public void shareHttpCallBody() {
    if (view.isWriteStoragePermissionGranted()) {

      final StringBuilder completeHttpCallData = getHttpCallData();

      executor.execute(new BackgroundTask<String>() {
        @Override
        public String onExecute() {
          return fileUtil.createLogFile(completeHttpCallData);
        }

        @Override
        public void onResult(String result) {
          if (!StringUtils.isEmpty(result)) {
            view.shareData(result);
          }
        }
      });
    } else {
      view.showMessageShareNotAvailable();
    }
  }

  private String getTextToCopy(int selectedItem) {
    HttpCallTab httpCallTab = HttpCallTab.byIndex(selectedItem);
    String dataToCopy;
    HttpHeader contentTypeHeader;
    if (httpCallTab == RESPONSE) {
      contentTypeHeader = httpCall.getResponseHeader(CONTENT_TYPE);
      dataToCopy = httpCall.getResponseBody();
    } else {
      contentTypeHeader = httpCall.getRequestHeader(CONTENT_TYPE);
      dataToCopy = httpCall.getPayload();
    }
    return getFormattedData(contentTypeHeader, dataToCopy);

  }

  private boolean contentHeadersPresent(HttpHeader contentTypeHeader) {
    return contentTypeHeader != null && contentTypeHeader.getValues().size() > 0;
  }

  public void onHttpCallBodyFormatted(int mode) {
    if (mode == REQUEST_MODE)
      requestViewLoaded = true;
    if (mode == RESPONSE_MODE)
      responseViewLoaded = true;
    if (requestViewLoaded && responseViewLoaded) {
      view.dismissProgressDialog();
    }
  }


  private StringBuilder getHttpCallData() {

    StringBuilder dataToCopy = new StringBuilder();
    HttpHeader contentTypeHeader;

    contentTypeHeader = httpCall.getRequestHeader(CONTENT_TYPE);
    String formattedRequestData = getFormattedData(contentTypeHeader, httpCall.getPayload());
    if (!StringUtils.isEmpty(formattedRequestData)) {
      dataToCopy.append("Request Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedRequestData);
      dataToCopy.append("\n");
    }

    contentTypeHeader = httpCall.getResponseHeader(CONTENT_TYPE);
    String formattedResponseData = getFormattedData(contentTypeHeader, httpCall.getResponseBody());

    if (!StringUtils.isEmpty(formattedResponseData)) {
      dataToCopy.append("Response Body");
      dataToCopy.append("\n");
      dataToCopy.append(formattedResponseData);
    }
    return dataToCopy;
  }

  private String getFormattedData(HttpHeader contentTypeHeader, String dataToCopy) {
    if (contentHeadersPresent(contentTypeHeader)) {
      ResponseFormatter formatter = this.formatterFactory.getFor(contentTypeHeader.getValues().get(0).getValue());
      return formatter.format(dataToCopy);
    }
    return "";
  }
}