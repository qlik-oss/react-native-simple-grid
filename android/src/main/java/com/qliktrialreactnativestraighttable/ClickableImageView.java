package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Map;

@SuppressLint("ViewConstructor")
public class ClickableImageView extends androidx.appcompat.widget.AppCompatImageView implements SelectionsObserver {
  DataCell cell = null;
  boolean selected = false;
  String scaleType = null;
  int defaultTextColor = Color.BLACK;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final CustomHorizontalScrollView scrollView;
  ClickableImageView(Context context, SelectionsEngine selectionsEngine, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    this.selectionsEngine = selectionsEngine;
    gestureDetector = new GestureDetector(getContext(), new ClickableImageView.SingleTapListener());
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
      selectionsEngine.selectionsChanged(this.scrollView, selection);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    gestureDetector.onTouchEvent(e);
    return true;
  }

  public void onSelectionsChanged(String s) {
    String received = SelectionsEngine.getKeyFrom(s);
    String me = SelectionsEngine.getKeyFrom(cell);
    if(received.equalsIgnoreCase(me)) {
      selected = !selected;
      updateBackgroundColor();
    }
  }

  private void alwaysFit() {
    ViewGroup parent = (ViewGroup) this.getParent();
    ViewGroup.LayoutParams layout = parent.getLayoutParams();
    layout.height = TableTheme.rowHeight;
    layout.width = TableTheme.rowHeight + parent.getPaddingLeft() + parent.getPaddingRight();
    parent.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_CENTER);
    scaleType = "alwaysFit";
  }

  private void stretchToFit(DataColumn column)          {
    ViewGroup parent = (ViewGroup) this.getParent();
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

    ViewGroup parent = (ViewGroup) this.getParent();
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

    ViewGroup parent = (ViewGroup) this.getParent();
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
    switch (column.representation.imageSize) {
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
    RelativeLayout container = (RelativeLayout) this.getParent();
    RelativeLayout wrapper = (RelativeLayout) container.getParent();
    setTranslationY(0);

    switch (column.representation.imagePosition) {
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

  public void onClear() {
    selected = false;
    updateBackgroundColor();
  }

  private void updateBackgroundColor() {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    RelativeLayout wrapper = (RelativeLayout) this.getParent().getParent();
    wrapper.setBackgroundColor(color);
  }

  public void onRecycled() {
    if (cell.isDim) {
      selectionsEngine.remove(this);
    }
  }

  class SingleTapListener extends GestureDetector.SimpleOnGestureListener {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
      handleSingleTap();
      return true;
    }
  }
}
