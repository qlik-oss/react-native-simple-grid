package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("ViewConstructor")
public class ClickableImageView extends androidx.appcompat.widget.AppCompatImageView implements Content {
  DataCell cell = null;
  boolean selected = false;
  String scaleType = null;
  final SelectionsEngine selectionsEngine;
  GestureDetector gestureDetector;
  final TableView tableView;
  final CellView cellView;
  Animation fadeIn;

  ClickableImageView(Context context, SelectionsEngine selectionsEngine, TableView tableView, CellView cellView) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.catalyst_fade_in);
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
    layout.height = tableView.rowHeight;
    layout.width = tableView.rowHeight + parent.getPaddingLeft() + parent.getPaddingRight();
    parent.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_CENTER);
    scaleType = "alwaysFit";
  }

  private void stretchToFit(DataColumn column)          {
    ViewGroup parent = (ViewGroup) cellView.getParent();
    ViewGroup.LayoutParams layout = parent.getLayoutParams();
    layout.height = tableView.rowHeight;
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
    layout.height = tableView.rowHeight;
    layout.width = Math.round(tableView.rowHeight * aspectRatioMultiplier) + parent.getPaddingLeft() + parent.getPaddingRight();
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
    layout.width = (int)column.width;
    layout.height = Math.round(column.width * aspectRatioMultiplier);
    parent.setLayoutParams(layout);

    ViewGroup grandparent = (ViewGroup) parent.getParent();
    ViewGroup.LayoutParams grandparentLayout = grandparent.getLayoutParams();
    grandparentLayout.width = column.width;
    grandparentLayout.height = Math.round(column.width * aspectRatioMultiplier);
    grandparent.setLayoutParams(grandparentLayout);
    this.setScaleType(ScaleType.FIT_XY);

    parent.setMinimumWidth((int)column.width);
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
    ViewGroup container = (ViewGroup) cellView.getParent();
    LinearLayout wrapper = (LinearLayout) container.getParent();
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
          setTranslationY(tableView.rowHeight / 2 - container.getMinimumHeight() / 2);
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
          setTranslationY(tableView.rowHeight - container.getMinimumHeight());
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
  public boolean isSelected() {
    return selected;
  }

  public void copyToClipBoard() {
    ImageShare imageShare = new ImageShare();
    BitmapDrawable drawable = (BitmapDrawable) this.getDrawable();
    if(drawable != null) {
      Bitmap bitmap = drawable.getBitmap();
      imageShare.share(bitmap, getContext());
    }
  }

  public String getCopyMenuString() {
    return "share";
  }
}
