package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

@SuppressLint("ViewConstructor")
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataCell cell = null;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final CustomHorizontalScrollView scrollView;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    this.selectionsEngine = selectionsEngine;
    defaultTextColor = getCurrentTextColor();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    gestureDetector.onTouchEvent(e);
    return true;
  }

  public void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    int textColor = selected ? Color.WHITE : defaultTextColor;
    setBackgroundColor(color);
    setTextColor(textColor);
  }

  @Override
  public void setGestureDetector(GestureDetector gestureDetector) {
    this.gestureDetector = gestureDetector;
  }

  @Override
  public void toggleSelected() {
    this.selected = !selected;
  }

  @Override
  public void setCell(DataCell cell) {
    this.cell = cell;
  }

  @Override
  public DataCell getCell() {
    return this.cell;
  }
}
