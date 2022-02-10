package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.json.JSONException;

import java.util.List;

public class EventUtils {
  public static void sendEventToJSFromView(CustomHorizontalScrollView contextView, String eventName) {
    if (contextView != null) {
      WritableMap event = Arguments.createMap();
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }
  public static void sendEventToJSFromView(CustomHorizontalScrollView contextView, String eventName, WritableMap event) {
    if (contextView != null) {
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }

  public static void sendOnColumnResize(CustomHorizontalScrollView contextView, List<DataColumn> dataColumns) {
    WritableArray widths = Arguments.createArray();
    for(int i = 0; i < dataColumns.size(); i++) {
      widths.pushDouble(PixelUtils.pxToDp(dataColumns.get(i).width));
    }
    WritableMap event = Arguments.createMap();
    event.putArray("widths", widths);
    EventUtils.sendEventToJSFromView(contextView, "onColumnsResized", event);
  }

  public static void sendOnHeaderTapped(CustomHorizontalScrollView contextView, DataColumn column) {
    WritableMap event = Arguments.createMap();
    try {
      Log.d("foo", column.toEvent());
      event.putString("column", column.toEvent());
      EventUtils.sendEventToJSFromView(contextView, "onHeaderPressed", event);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
