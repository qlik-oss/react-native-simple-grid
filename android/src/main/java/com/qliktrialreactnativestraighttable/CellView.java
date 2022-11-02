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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("ViewConstructor")
public class CellView extends LinearLayout implements SelectionsObserver {
  Content content = null;
  final DragBoxEventHandler dragBoxEventHandler;
  final SelectionsEngine selectionsEngine;
  final TableView tableView;
  final boolean firstColumn;
  GestureDetector gestureDetector;
  int padding = (int)PixelUtils.dpToPx(16);

  CellView(Context context, String type, SelectionsEngine selectionsEngine, TableView tableView, boolean firstColumn) {
    super(context);
    this.tableView = tableView;
    if(type.equals("text")) {
      ClickableTextView textView = new ClickableTextView(context, selectionsEngine, tableView, this);
      textView.setPadding(padding, 0, padding, 0);
      content = textView;
    } else if(type.equals("image")) {
      content = new ClickableImageView(context, selectionsEngine, tableView, this);
    } else if(type.equals("miniChart")) {
      content = new MiniChartView(context);
      this.setPadding(padding, 0, padding, 0);
    }

    this.selectionsEngine = selectionsEngine;
    this.firstColumn = firstColumn;
    this.dragBoxEventHandler = tableView.dragBoxEventHandler;

    dragBoxEventHandler.addDragBoxListener((boxBounds, column) -> handleDragBoxDrag(boxBounds, column));
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
    String copyString = tableView.getTranslation("menu", "copy");
    View.OnCreateContextMenuListener onCreateContextMenuListener = (contextMenu, view, contextMenuInfo) -> contextMenu.add(0, 0, 0, copyString).setOnMenuItemClickListener(handleMenuItemClick);
    View contentView = (View) content;
    contentView.setOnCreateContextMenuListener(onCreateContextMenuListener);
    this.addView(contentView);
  }

  public void handleDragBoxDrag(Rect dragBoxBounds, int columnId) {
    DataCell cell = content.getCell();
    if(cell == null || columnId != cell.colIdx) {
      return;
    }
    Rect cellBounds = getBounds();
    if(cellBounds == null) {
      return;
    }
    boolean hasIntersect = dragBoxBounds.intersect(cellBounds);
    if(!this.content.isSelected() && hasIntersect) {
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
      if(firstColumn) {
        tableView.offsetDescendantRectToMyCoords(this, bounds);
      } else {
        tableView.rootLayout.offsetDescendantRectToMyCoords(this, bounds);
      }
    } catch (IllegalArgumentException e) {
      // ignore if cell offscreen
    }
    return bounds;
  }

  public void handleSingleTap() {
    DataCell cell = content.getCell();
    if (cell.isDim) {
      Rect bounds = getBounds();
      if(!content.isSelected()) {
        tableView.showDragBox(bounds, cell.colIdx);
      } else {
        tableView.hideDragBoxes();
      }
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
