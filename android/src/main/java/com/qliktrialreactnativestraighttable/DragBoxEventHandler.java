package com.qliktrialreactnativestraighttable;

import java.util.ArrayList;

public class DragBoxEventHandler {
  DragBox dragBox, firstColumnDragBox;
  ArrayList<DragBoxListener> listeners = new ArrayList<DragBoxListener> ();
  public void setDragBoxes (DragBox dragBox, DragBox firstColumnDragBox) {
    this.dragBox = dragBox;
    this.firstColumnDragBox = firstColumnDragBox;
  }
  public void setDragBoxListener (DragBoxListener listener) {
    this.listeners.add(listener);
  }
  public void fireEvent(String eventType, boolean firstColumn) {
    for(DragBoxListener listener : listeners) {
      switch(eventType) {
        case "dragged": {
          if(firstColumn) {
            listener.onDrag(this.firstColumnDragBox);
            return;
          }
          listener.onDrag(this.dragBox);
        }
      }
    }
  }
}
