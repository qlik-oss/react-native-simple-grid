package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("ViewConstructor")
public class ClickableImageView extends androidx.appcompat.widget.AppCompatImageView implements Content {
  DataCell cell = null;
  boolean selected = false;
  String scaleType = null;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final CustomHorizontalScrollView scrollView;
  final CellView cellView;

  ClickableImageView(Context context, SelectionsEngine selectionsEngine, CustomHorizontalScrollView scrollView, CellView cellView) {
    super(context);
    this.scrollView = scrollView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    gestureDetector.onTouchEvent(e);
    return true;
  }

  private void alwaysFit() {
    ViewGroup parent = (ViewGroup) cellView.getParent();
    ViewGroup.LayoutParams layout = parent.getLayoutParams();
    layout.height = TableTheme.rowHeight;
    layout.width = TableTheme.rowHeight + parent.getPaddingLeft() + parent.getPaddingRight();
    parent.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_CENTER);
    scaleType = "alwaysFit";
  }

  private void stretchToFit(DataColumn column)          {
    ViewGroup parent = (ViewGroup) cellView.getParent();
    ViewGroup.LayoutParams layout = parent.getLayoutParams();
    layout.height = TableTheme.rowHeight;
    layout.width = column.width;
    parent.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_XY);
    scaleType = "stretchToFit";

  }

  private void fitToHeight(Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();
    float aspectRatioMultiplier = width/height;

    ViewGroup parent = (ViewGroup) cellView.getParent();
    ViewGroup.LayoutParams layout = parent.getLayoutParams();
    layout.height = TableTheme.rowHeight;
    layout.width = Math.round(TableTheme.rowHeight * aspectRatioMultiplier) + parent.getPaddingLeft() + parent.getPaddingRight();
    parent.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_XY);
    scaleType = "fitToHeight";

  }

  private void fitToWidth(DataColumn column, Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();
    float aspectRatioMultiplier = height/width;

    ViewGroup parent = (ViewGroup) cellView.getParent();
    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) parent.getLayoutParams();
    layout.width = column.width;
    layout.height = Math.round(column.width * aspectRatioMultiplier);
    parent.setLayoutParams(layout);

    ViewGroup grandparent = (ViewGroup) parent.getParent();
    ViewGroup.LayoutParams grandparentLayout = grandparent.getLayoutParams();
    grandparentLayout.width = column.width;
    grandparentLayout.height = Math.round(column.width * aspectRatioMultiplier);
    grandparent.setLayoutParams(grandparentLayout);
    this.setScaleType(ScaleType.FIT_XY);

    parent.setMinimumWidth(column.width);
    parent.setMinimumHeight(Math.round(column.width * aspectRatioMultiplier));
    scaleType = "fitToWidth";
  }

  public void setSizing(DataColumn column, Bitmap image) {
    switch (column.imageSize) {
      case "alwaysFit":
        alwaysFit();
        break;
      case "fill":
        stretchToFit(column);
        break;
      case "fitHeight":
        fitToHeight(image);
        break;
      case "fitWidth":
        fitToWidth(column, image);
        break;
    }
  }

  public void setAlignment(DataColumn column) {
    ViewGroup container = (ViewGroup) cellView.getParent();
    LinearLayout wrapper = (LinearLayout) container.getParent();
    setTranslationY(0);

    switch (column.imagePosition) {
      case "topCenter":
        wrapper.setGravity(Gravity.LEFT);
        break;
      case "bottomCenter":
        wrapper.setGravity(Gravity.RIGHT);
        break;
      case "centerCenter":
        wrapper.setGravity(Gravity.CENTER);
        if (scaleType.equals("fitToWidth")) {
          setTranslationY(TableTheme.rowHeight / 2 - container.getMinimumHeight() / 2);
        }
        break;
      case "centerLeft":
        if (scaleType.equals("fitToWidth")) {
          wrapper.setGravity(Gravity.TOP);
          break;
        }
        wrapper.setGravity(Gravity.CENTER);
        break;
      case "centerRight":
        if (scaleType.equals("fitToWidth")) {
          wrapper.setGravity(Gravity.BOTTOM);
          setTranslationY(TableTheme.rowHeight - container.getMinimumHeight());
          break;
        }
        wrapper.setGravity(Gravity.CENTER);
        break;
    }
  }

  public void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    ViewGroup wrapper = (ViewGroup) cellView.getParent().getParent();
    wrapper.setBackgroundColor(color);
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
