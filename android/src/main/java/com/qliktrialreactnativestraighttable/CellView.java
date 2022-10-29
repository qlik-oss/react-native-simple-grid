package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
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
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final CustomHorizontalScrollView scrollView;
  int padding = (int)PixelUtils.dpToPx(16);

  CellView(Context context, String type, SelectionsEngine selectionsEngine, CustomHorizontalScrollView scrollView) {
    super(context);
    this.setPadding(padding, 0, padding, 0);
    if(type.equals("text")) {
      content = new ClickableTextView(context, selectionsEngine, scrollView);
    } else if(type.equals("image")) {
      content = new ClickableImageView(context, selectionsEngine, scrollView, this);
    } else if(type.equals("miniChart")) {
      content = new MiniChartView(context);
    }
    this.scrollView = scrollView;
    this.selectionsEngine = selectionsEngine;
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

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    this.getChildAt(0).invalidate();
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
    }
  }

  public void handleSingleTap() {
    DataCell cell = content.getCell();
    if (cell.isDim) {
      String selection = SelectionsEngine.getSignatureFrom(cell);
      selectionsEngine.selectionsChanged(this.scrollView, selection);
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
