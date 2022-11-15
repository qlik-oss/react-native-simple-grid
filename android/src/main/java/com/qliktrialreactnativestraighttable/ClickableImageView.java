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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
    layout.height = tableView.rowHeight;
    layout.width = tableView.rowHeight;

    this.setLayoutParams(layout);
    this.setScaleType(ScaleType.FIT_CENTER);

    scaleType = "alwaysFit";
  }

  private void stretchToFit(DataColumn column)          {
    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
    layout.height = tableView.rowHeight;
    layout.width = column.width;
    setLayoutParams(layout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "stretchToFit";
  }

  private void fitToHeight(Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();
    float aspectRatioMultiplier = width/height;

    RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
    layout.height = tableView.rowHeight;
    layout.width = Math.round(tableView.rowHeight * aspectRatioMultiplier);
    setLayoutParams(layout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "fitToHeight";
  }

  private void fitToWidth(DataColumn column, Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();
    float aspectRatioMultiplier = height/width;

    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    setLayoutParams(layout);

    LinearLayout.LayoutParams cellLayout = (LinearLayout.LayoutParams) cellView.getLayoutParams();
    cellLayout.width = column.width;
    cellLayout.height = Math.round(column.width * aspectRatioMultiplier);
    cellView.setLayoutParams(cellLayout);

    this.setScaleType(ScaleType.FIT_XY);

    cellView.setMinimumWidth((int)column.width);
    cellView.setMinimumHeight(Math.round(column.width * aspectRatioMultiplier));
    scaleType = "fitToWidth";
  }

  public void setSizing(DataColumn column, Bitmap image) {
    alwaysFit();
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
    RelativeLayout wrapper = (RelativeLayout) cellView;
    setTranslationY(0);

    switch (column.representation.imagePosition) {
      case "topCenter":
        wrapper.setGravity(Gravity.LEFT);
        break;
      case "bottomCenter":
        wrapper.setGravity(Gravity.RIGHT);
        break;
      case "centerCenter":
        if (scaleType.equals("fitToWidth")) {
          setTranslationY((float) (tableView.rowHeight / 2 - cellView.getMinimumHeight() / 2));
        }
        wrapper.setGravity(Gravity.CENTER);
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
          setTranslationY(tableView.rowHeight - cellView.getMinimumHeight());
          break;
        }
        wrapper.setGravity(Gravity.CENTER);
        break;
    }
  }

  public void updateBackgroundColor(boolean shouldAnimate) {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    ViewGroup wrapper = (ViewGroup) cellView.getParent();
    wrapper.setBackgroundColor(color);
    if(shouldAnimate) {
      startAnimation(fadeIn);
    }
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
