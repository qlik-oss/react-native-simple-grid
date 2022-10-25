package com.qliktrialreactnativestraighttable;

import android.graphics.Rect;

import java.util.ArrayList;

public class DragBoxEventRegistrar {
  ArrayList<DragBoxListener> listeners = new ArrayList<DragBoxListener> ();
  public void setDragBoxListener (DragBoxListener listener) {
    this.listeners.add(listener);
  }
  public void fireEvent(String eventType, Rect bounds) {
    for(DragBoxListener listener : listeners) {
      switch(eventType) {
        case "dragged": {
          listener.onDrag(bounds);
        }
      }
    }
  }
}
