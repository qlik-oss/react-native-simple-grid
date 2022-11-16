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

@SuppressLint("ViewConstructor")
public class ClickableImageView extends androidx.appcompat.widget.AppCompatImageView implements Content {
  DataCell cell = null;
  boolean selected = false;
  String scaleType = null;
  final SelectionsEngine selectionsEngine;
  final TableView tableView;
  final CellView cellView;
  Animation fadeIn;
  GestureDetector gestureDetector;

  ClickableImageView(Context context, SelectionsEngine selectionsEngine, TableView tableView, CellView cellView) {
    super(context);
    this.tableView = tableView;
    this.selectionsEngine = selectionsEngine;
    this.cellView = cellView;
    fadeIn = AnimationUtils.loadAnimation(context, R.anim.catalyst_fade_in);
  }

  private void alwaysFit() {
    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(tableView.rowHeight, tableView.rowHeight);
    this.setLayoutParams(layout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "alwaysFit";
  }

  private void stretchToFit(DataColumn column)          {
    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(column.width, tableView.rowHeight);
    this.setLayoutParams(layout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "stretchToFit";
  }

  private void fitToHeight(float aspectRatioMultiplier) {
    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(Math.round(tableView.rowHeight * aspectRatioMultiplier), tableView.rowHeight);
    this.setLayoutParams(layout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "fitToHeight";
  }

  private void fitToWidth(DataColumn column, float aspectRatioMultiplier) {
    RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    setLayoutParams(layout);

    RelativeLayout parent = (RelativeLayout) getParent();
    RelativeLayout.LayoutParams wrapperLayout = (RelativeLayout.LayoutParams) parent.getLayoutParams();
    wrapperLayout.width = column.width;
    wrapperLayout.height = Math.round(column.width * aspectRatioMultiplier);
    parent.setLayoutParams(wrapperLayout);

    this.setScaleType(ScaleType.FIT_XY);

    scaleType = "fitToWidth";
  }

  public void setSizing(DataColumn column, Bitmap image) {
    float height = image.getHeight();
    float width = image.getWidth();

//    RelativeLayout parent = (RelativeLayout) getParent();
//    RelativeLayout.LayoutParams wrapperLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//    parent.setLayoutParams(wrapperLayout);

    switch (column.representation.imageSize) {
      case "fill":
        stretchToFit(column);
        break;
      case "fitHeight":
        fitToHeight(width/height);
        break;
      case "fitWidth":
        fitToWidth(column, height/width);
        break;
      default:
      case "alwaysFit":
        alwaysFit();
        break;
    }
  }

  public void setAlignment(DataColumn column) {
    RelativeLayout wrapper = (RelativeLayout) getParent();
    setTranslationY(0);

    if(wrapper == null) {
      return;
    }

    post(() -> {
      switch (column.representation.imagePosition) {
        case "topCenter":
          wrapper.setGravity(Gravity.LEFT);
          break;
        case "bottomCenter":
          wrapper.setGravity(Gravity.RIGHT);
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
            setTranslationY(tableView.rowHeight - wrapper.getMeasuredHeight());
            break;
          }
          wrapper.setGravity(Gravity.CENTER);
          break;
        default:
        case "centerCenter":
          if (scaleType.equals("fitToWidth")) {
            int h = wrapper.getMeasuredHeight();
            int h2 = tableView.rowHeight;
            setTranslationY((float) (tableView.rowHeight - wrapper.getMeasuredHeight()) / 2);
          }
          wrapper.setGravity(Gravity.CENTER);
          break;
      }
    });
  }

  public void updateBackgroundColor(boolean shouldAnimate) {
    int color = selected ? TableTheme.selectedBackground : Color.TRANSPARENT;
    cellView.setBackgroundColor(color);
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
