package com.qliktrialreactnativestraighttable;

import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class EventUtils {
  public static void sendEventToJSFromView(View view, String eventName) {
    WritableMap event = Arguments.createMap();
    ReactContext context = (ReactContext) view.getContext();
    context.getJSModule(RCTEventEmitter.class).receiveEvent(view.getId(), eventName, event);
  }
}
