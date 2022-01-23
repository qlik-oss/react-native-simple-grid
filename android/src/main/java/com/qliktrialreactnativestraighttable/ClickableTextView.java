package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements  SelectionsObserver {
  DataCell cell = null;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine) {
    super(context);

    this.selectionsEngine = selectionsEngine;
    defaultTextColor = getCurrentTextColor();
    gestureDetector = new GestureDetector(getContext(), new DoubleTapListener());
  }

  public void setData(DataCell cell) {
    this.cell = cell;
    // check to see if I'm here
    if (cell.isDim) {
      selectionsEngine.observe(this);
      selected = selectionsEngine.contains(cell);
      updateBackgroundColor();
    }
  }

  public void handleSingleTap() {
    if (cell.isDim) {
      String selection = SelectionsEngine.getSignatureFrom(cell);
      selectionsEngine.selectionsChanged(selection);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    gestureDetector.onTouchEvent(e);
    return  true;
  }

  public void onSelectionsChanged(String s) {
    String received = SelectionsEngine.getKeyFrom(s);
    String me = SelectionsEngine.getKeyFrom(cell);
    if(received.equalsIgnoreCase(me)) {
      selected = !selected;
      updateBackgroundColor();
    }
  }

  public  void onClear() {
    selected = false;
    updateBackgroundColor();
  }

  private void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    int textColor = selected ? Color.WHITE : defaultTextColor;
    setBackgroundColor(color);
    setTextColor(textColor);
  }

  public void onRecycled() {
    if (cell.isDim) {
      selectionsEngine.remove(this);
    }
  }

  class DoubleTapListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
      handleSingleTap();
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
      EventUtils.sendEventToJSFromView("onDoubleTap");
      return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
      return false;
    }
  }
}
