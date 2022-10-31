package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.MotionEvent;

@SuppressLint("ViewConstructor")
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataCell cell = null;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final TableView tableView;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, TableView tableView) {
    super(context);
    this.tableView = tableView;
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
    postInvalidate();
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

  @Override
  public void setSelected(boolean value) {
    selected = value;
  }
}
