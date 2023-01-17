package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.text.TextUtils;
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
import android.widget.ScrollView;

@SuppressLint("ViewConstructor")
public class CellView extends RelativeLayout implements SelectionsObserver {
  String type = "";
  Content content = null;
  DataRow row;
  DataColumn column;
  FrameLayout wrapper = null;
  RelativeLayout.LayoutParams wrapperLayout = null;
  final DragBoxEventHandler dragBoxEventHandler;
  final SelectionsEngine selectionsEngine;
  final TableView tableView;
  final boolean isInFirstColumnRecyclerView;
  final View.OnCreateContextMenuListener onCreateContextMenuListener;
  GestureDetector gestureDetector;
  static final int PADDING = TableTheme.CellPadding;
  static final int PADDING_X_2 = TableTheme.CellPadding * 2;

  @SuppressLint("ClickableViewAccessibility")
  CellView(Context context, String type, SelectionsEngine selectionsEngine, TableView tableView, boolean isInFirstColumnRecyclerView, DataColumn dataColumn) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.isInFirstColumnRecyclerView = isInFirstColumnRecyclerView;
    this.dragBoxEventHandler = tableView.dragBoxEventHandler;

    createContent(type, dataColumn);

    dragBoxEventHandler.addDragBoxListener(this::handleDragBoxDrag);
    gestureDetector = new GestureDetector(getContext(), new CellView.SingleTapListener());

    MenuItem.OnMenuItemClickListener handleMenuItemClick = item -> {
      switch (item.getItemId()) {
        case 0: // Copy
          copyCell();
          break;
        case 1: // Expand
        default:
          expandRow();
      }
      return true;
    };
    String copyString = tableView.getTranslation("menu", content.getCopyMenuString());
    String expandString = tableView.getTranslation("menu", "expand");

    onCreateContextMenuListener = (contextMenu, view, contextMenuInfo) -> {
      contextMenu.add(0, 0, 0, copyString).setOnMenuItemClickListener(handleMenuItemClick);
      contextMenu.add(0, 1, 1, expandString).setOnMenuItemClickListener(handleMenuItemClick);
    };
    setOnCreateContextMenuListener(onCreateContextMenuListener);
    addContentView(type);
  }

  private void createContent(String type, DataColumn dataColumn) {
    switch (type) {
      case "text":
        ClickableTextView textView = new ClickableTextView(getContext(), selectionsEngine, tableView, this, dataColumn);
        textView.setPadding(PADDING, 0, PADDING, 0);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        content = textView;
        break;
      case "image":
        wrapper = new FrameLayout(getContext());
        wrapperLayout = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        content = new ClickableImageView(getContext(), selectionsEngine, tableView, this);
        wrapper.addView((View) content);
        break;
      case "miniChart":
        content = new MiniChartView(getContext());
        this.setPadding(PADDING, 0, PADDING, 0);
        break;
    }
    this.type = type;
  }

  private void addContentView(String type) {
    if(type.equals("image")) {
      this.addView(wrapper, wrapperLayout);
    } else {
      View contentView = (View) content;
      this.addView(contentView);
    }
  }

  public void convertCellContentType(String type, DataColumn dataColumn) {
    if(this.type.equals(type)) {
      return;
    }
    wrapper = null;
    wrapperLayout = null;
    column = dataColumn;
    removeAllViews();
    createContent(type, dataColumn);
    addContentView(type);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    gestureDetector.onTouchEvent(event);
    if(content != null) {
      return content.handleTouch(event);
    }
    return super.onTouchEvent(event);
  }

  public void handleDragBoxDrag(Rect dragBoxBounds, int columnId) {
    DataCell cell = content.getCell();
    if(cell == null || columnId != cell.rawColIdx) {
      return;
    }
    Rect cellBounds = getBounds();
    boolean hasIntersect = dragBoxBounds.intersect(cellBounds);
    if(!this.content.isSelected() && hasIntersect) {
      selectCell();
    }
  }

  private void expandRow() {
    EventUtils.sendOnExpand(tableView, row);
  }

  private void copyCell(){
    if(content != null) {
      content.copyToClipBoard();
    }
  }

  public void setData(DataCell cell, DataRow row, DataColumn column) {
    this.row = row;
    this.column = column;
    content.setCellData(cell, row, column);
    if (cell.isDim) {
      selectionsEngine.observe(this);
      content.setSelected(selectionsEngine.contains(cell));
      content.updateBackgroundColor(false);
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
      if(isInFirstColumnRecyclerView) {
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
    if (cell != null && cell.isDim) {
      Rect bounds = getBounds();
      if(!content.isSelected()) {
        tableView.showDragBox(bounds, cell.rawColIdx);
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
      content.updateBackgroundColor(true);
    }
  }

  public void onClear() {
    content.setSelected(false);
    content.updateBackgroundColor(false);
  }

  public void onRecycled() {
    DataCell cell = content.getCell();
    if (cell != null && cell.isDim) {
      selectionsEngine.remove(this);
    }
  }

  public void updateWidth(int newWidth) {
    if(type.equals("image") && column.representation.imageSize.equals("fill") || column.representation.imageSize.equals("fill")) {
      FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
      RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
      wrapper.setLayoutParams(layout);
      ((View) content).setLayoutParams(frameLayout);
    }
    ViewGroup.LayoutParams params = getLayoutParams();
    params.width = newWidth;
    setLayoutParams(params);
  }

  @Override
  public void requestLayout() {
    super.requestLayout();
    post(measureAndLayout);
  }

  private final Runnable measureAndLayout = new Runnable() {
    @Override
    public void run() {
      measure(
        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };

  class SingleTapListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
      handleSingleTap();
      return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
      ((View) content).showContextMenu(e.getX(), e.getY());
    }
  }
}
