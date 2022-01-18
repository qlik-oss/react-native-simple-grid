package com.qliktrialreactnativestraighttable;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

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
}
