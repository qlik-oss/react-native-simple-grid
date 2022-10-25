package com.qliktrialreactnativestraighttable;

import java.util.ArrayList;

public class DragBoxEventHandler {
  private DragBox dragBox;
  ArrayList<DragBoxListener> listeners = new ArrayList<DragBoxListener> ();
  public void setDragBox (DragBox dragBox) {
    this.dragBox = dragBox;
  }
  public void setDragBoxListener (DragBoxListener listener) {
    this.listeners.add(listener);
  }
  public void fireEvent(String eventType, int columnId) {
    for(DragBoxListener listener : listeners) {
      switch(eventType) {
        case "dragged": {
          listener.onDrag(this.dragBox, columnId);
        }
      }
    }
  }
}
