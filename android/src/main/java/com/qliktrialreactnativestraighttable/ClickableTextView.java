package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressLint("ViewConstructor")
public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView implements Content {
  DataCell cell = null;
  final CellView cellView;
  boolean selected = false;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final TableView tableView;
  Animation fadeIn;
  ClickableTextView(Context context, SelectionsEngine selectionsEngine, TableView tableView, CellView cellView) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
    defaultTextColor = getCurrentTextColor();
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.catalyst_fade_in);
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
    cellView.setBackgroundColor(color);
    setTextColor(textColor);
    postInvalidate();
    startAnimation(fadeIn);
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

  public boolean isSelected(){
    return selected;
  }
}
