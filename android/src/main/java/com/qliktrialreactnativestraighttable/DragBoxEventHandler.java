package com.qliktrialreactnativestraighttable;

import android.graphics.Rect;

import java.util.ArrayList;

public class DragBoxEventHandler {
  final TableView tableView;
  DragBox dragBox, firstColumnDragBox;
  ArrayList<DragBoxListener> listeners = new ArrayList<DragBoxListener> ();

  public DragBoxEventHandler(TableView tableView) {
    this.tableView = tableView;
  }

  public void setDragBoxes (DragBox dragBox, DragBox firstColumnDragBox) {
    this.dragBox = dragBox;
    this.firstColumnDragBox = firstColumnDragBox;
  }
  public void setDragBoxListener (DragBoxListener listener) {
    this.listeners.add(listener);
  }
  public void fireEvent(String eventType, int columnId, boolean firstColumn) {
    for(DragBoxListener listener : listeners) {
      switch(eventType) {
        case "dragged": {
          if(firstColumn) {
            Rect bounds = new Rect(firstColumnDragBox.bounds);
            tableView.offsetDescendantRectToMyCoords(firstColumnDragBox, bounds);
            listener.onDrag(bounds, columnId);
            return;
          }
          listener.onDrag(dragBox.bounds, columnId);
        }
      }
    }
  }
}
