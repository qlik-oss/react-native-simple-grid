package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("ViewConstructor")
public class CellView extends LinearLayout implements SelectionsObserver {
  Content content = null;
  final DragBoxEventHandler dragBoxEventHandler;
  final SelectionsEngine selectionsEngine;
  final TableView tableView;
  GestureDetector gestureDetector;
  int padding = (int)PixelUtils.dpToPx(16);

  CellView(Context context, String type, SelectionsEngine selectionsEngine, TableView tableView) {
    super(context);
    this.setPadding(padding, 0, padding, 0);
    if(type.equals("text")) {
      content = new ClickableTextView(context, selectionsEngine, tableView);
    } else if(type.equals("image")) {
      content = new ClickableImageView(context, selectionsEngine, tableView, this);
    } else if(type.equals("miniChart")) {
      content = new MiniChartView(context);
    }
    this.selectionsEngine = selectionsEngine;
    this.tableView = tableView;
    this.dragBoxEventHandler = tableView.dragBoxEventHandler;

    dragBoxEventHandler.setDragBoxListener((dragBox, column) -> handleDragBoxDrag(dragBox, column));

    gestureDetector = new GestureDetector(getContext(), new CellView.SingleTapListener());
    content.setGestureDetector(gestureDetector);

    MenuItem.OnMenuItemClickListener handleMenuItemClick = item -> {
      switch (item.getItemId()) {
        case 0: // Copy
        default:
          copyCell(context);
      }
      return true;
    };
    View.OnCreateContextMenuListener onCreateContextMenuListener = (contextMenu, view, contextMenuInfo) -> contextMenu.add(0, 0, 0, "Copy").setOnMenuItemClickListener(handleMenuItemClick);
    View contentView = (View) content;
    contentView.setOnCreateContextMenuListener(onCreateContextMenuListener);
    this.addView(contentView);
  }

  public void handleDragBoxDrag(DragBox dragBox, int columnId) {
    if(columnId != content.getCell().colIdx) {
      return;
    }
    Rect cellBounds = getBounds();
    if(cellBounds == null) {
      return;
    }
    if(!this.content.isSelected() && dragBox.checkCollision(cellBounds)) {
      selectCell();
    }
  }

  private void copyCell(Context context){
    DataCell cell = content.getCell();
    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    ClipData clip = ClipData.newPlainText(cell.qText, cell.qText);
    clipboard.setPrimaryClip(clip);
  }

  public void setData(DataCell cell) {
    content.setCell(cell);
    if (cell.isDim) {
      selectionsEngine.observe(this);
      content.setSelected(selectionsEngine.contains(cell));
      content.updateBackgroundColor();
      postInvalidate();
    }
  }

  private void selectCell() {
    DataCell cell = content.getCell();
    String selection = SelectionsEngine.getSignatureFrom(cell);
    selectionsEngine.selectionsChanged(this.tableView, selection);
  }

  private Rect getBounds() {
    Rect bounds = new Rect();
    this.getDrawingRect(bounds);
    try {
      tableView.rootLayout.offsetDescendantRectToMyCoords(this, bounds);
    } catch (IllegalArgumentException e) {
      // ignore if cell offscreen
      return null;
    }
    return bounds;
  }

  public void handleSingleTap() {
    DataCell cell = content.getCell();
    if (cell.isDim) {
      Rect bounds = getBounds();
      tableView.addDragBox(bounds, cell.colIdx);
      selectCell();
    }
  }

  public void onSelectionsChanged(String s) {
    DataCell cell = content.getCell();
    String received = SelectionsEngine.getKeyFrom(s);
    String me = SelectionsEngine.getKeyFrom(cell);
    if(received.equalsIgnoreCase(me)) {
      content.toggleSelected();
      content.updateBackgroundColor();
    }
  }

  public void onClear() {
    content.setSelected(false);
    content.updateBackgroundColor();
  }

  public void onRecycled() {
    DataCell cell = content.getCell();
    if (cell != null && cell.isDim) {
      selectionsEngine.remove(this);
    }
  }

  class SingleTapListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
      handleSingleTap();
      return true;
    }
    @Override
    public void onLongPress(MotionEvent e) {
      ((View) content).showContextMenu();
    }
  }
}
