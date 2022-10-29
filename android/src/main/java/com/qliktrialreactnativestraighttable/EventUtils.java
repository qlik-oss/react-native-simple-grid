package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.json.JSONException;

import java.util.List;

public class EventUtils {
  public static void sendEventToJSFromView(View contextView, String eventName) {
    if (contextView != null) {
      WritableMap event = Arguments.createMap();
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }
  public static void sendEventToJSFromView(View contextView, String eventName, WritableMap event) {
    if (contextView != null) {
      ReactContext context = (ReactContext) contextView.getContext();
      // here the documentation is still using the old receiveEvent, so not sure what to use????
      context.getJSModule(RCTEventEmitter.class).receiveEvent(contextView.getId(), eventName, event);
    }
  }

  public static void sendOnHeaderTapped(View contextView, DataColumn column) {
    WritableMap event = Arguments.createMap();
    try {
      String columnJSONString = column.toEvent();
      Log.d("foo", columnJSONString);
      event.putString("column", columnJSONString);
      EventUtils.sendEventToJSFromView(contextView, "onHeaderPressed", event);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
