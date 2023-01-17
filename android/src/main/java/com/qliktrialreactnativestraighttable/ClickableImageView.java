package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.method.MovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

@SuppressLint("ViewConstructor")
public class ClickableImageView extends androidx.appcompat.widget.AppCompatImageView implements Content {
  int imageHeight;
  int imageWidth;

  DataCell cell = null;
  boolean selected = false;
  String scaleType = null;
  final SelectionsEngine selectionsEngine;
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

  private void fitToHeight() {
    this.setScaleType(ScaleType.FIT_XY);

    imageHeight = tableView.rowHeight;
    imageWidth = imageHeight;

    scaleType = "fitToHeight";
  }

  private void stretchToFit(DataColumn column) {
    this.setScaleType(ScaleType.FIT_XY);

    imageHeight = tableView.rowHeight;
    imageWidth = column.width;

    scaleType = "stretchToFit";
  }

  private void alwaysFit(float aspectRatioMultiplier) {
    this.setScaleType(ScaleType.FIT_XY);

    imageHeight = tableView.rowHeight;
    imageWidth = Math.round(tableView.rowHeight * aspectRatioMultiplier);

    scaleType = "alwaysFit";
  }

  private void fitToWidth(DataColumn column, float aspectRatioMultiplier) {
    this.setScaleType(ScaleType.MATRIX);

    imageHeight = Math.round(column.width * aspectRatioMultiplier);
    imageWidth = column.width;

    scaleType = "fitToWidth";
  }

  public void scaleAndPositionImage(DataColumn column, Bitmap image) {
    setSizing(column, image);
    setAlignment(column);
    
    FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(imageWidth, imageHeight);
    setLayoutParams(layout);
  }

  private void shrinkParentToBounds() {
    FrameLayout wrapper = (FrameLayout) getParent();
    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(imageWidth, imageHeight);
    wrapper.setLayoutParams(layout);
  }

  private void setSizing(DataColumn column, Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();

    switch (column.representation.imageSize) {
      case "fill":
        stretchToFit(column);
        shrinkParentToBounds();
        break;
      case "fitHeight":
        fitToHeight();
        shrinkParentToBounds();
        break;
      case "fitWidth":
        fitToWidth(column, height/width);
        FrameLayout wrapper = (FrameLayout) getParent();
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        wrapper.setLayoutParams(layout);
        break;
      default:
      case "alwaysFit":
        alwaysFit(width/height);
        shrinkParentToBounds();
        break;
    }
  }

  private void setAlignment(DataColumn column) {
    setTranslationY(0);

    switch (column.representation.imagePosition) {
      case "topCenter":
        cellView.setGravity(Gravity.LEFT);
        break;
      case "bottomCenter":
        cellView.setGravity(Gravity.RIGHT);
        break;
      case "centerLeft":
        if (scaleType.equals("fitToWidth")) {
          cellView.setGravity(Gravity.TOP);
          break;
        }
        cellView.setGravity(Gravity.CENTER);
        break;
      case "centerRight":
        if (scaleType.equals("fitToWidth")) {
          cellView.setGravity(Gravity.BOTTOM);
          setTranslationY(tableView.rowHeight - imageHeight);
          break;
        }
        cellView.setGravity(Gravity.CENTER);
        break;
      default:
      case "centerCenter":
        if (scaleType.equals("fitToWidth")) {
          setTranslationY((float) (tableView.rowHeight - imageHeight) / 2);
        }
        cellView.setGravity(Gravity.CENTER);
        break;
    }
  }

  public void updateBackgroundColor(boolean shouldAnimate) {
    int bgColor = cell.cellBackgroundColorValid ? cell.cellBackgroundColor : Color.TRANSPARENT;
    int color = selected ? TableTheme.selectedBackground : bgColor;
    cellView.setBackgroundColor(color);
    if(shouldAnimate) {
      startAnimation(fadeIn);
    }
  }

  @Override
  public boolean handleTouch(MotionEvent e) {
    return true;
  }

  @Override
  public void toggleSelected() {
    this.selected = !selected;
  }

  @Override
  public void setCellData(DataCell cell, DataRow row, DataColumn column) {
    this.cell = cell;
    cellView.setBackgroundColor(cell.cellBackgroundColorValid ? cell.cellBackgroundColor : Color.TRANSPARENT);
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
