package com.qliktrialreactnativestraighttable;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.List;

public class EventUtils {
  static View contextView = null;
  public static void sendEventToJSFromView(String eventName) {
    if (contextView != null) {
      WritableMap event = Arguments.createMap();
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }
  public static void sendEventToJSFromView(String eventName, WritableMap event) {
    if (contextView != null) {
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }

  public static void sendOnColumnResize(List<DataColumn> dataColumns) {
    WritableArray widths = Arguments.createArray();
    for(int i = 0; i < dataColumns.size(); i++) {
      widths.pushDouble(PixelUtils.pxToDp(dataColumns.get(i).width));
    }
    WritableMap event = Arguments.createMap();
    event.putArray("widths", widths);
    EventUtils.sendEventToJSFromView("onColumnsResized", event);
  }
}
