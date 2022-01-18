package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GrabberView extends LinearLayout {
  GrabberButton grabberButton;
  Paint linePaint = new Paint();
  CustomHorizontalScrollView scrollView;
  DataProvider dataProvider = null;
  AutoLinearLayout headerView = null;
  AutoLinearLayout footerView = null;
  List<GrabberView> grabbers = null;
  RecyclerView recyclerView = null;
  ScreenGuideView screenGuideView = null;
  private final int column;
  private boolean isLastColumn = false;
  boolean pressed = false;

  class TouchListener implements OnTouchListener {
    float dX = 0;
    float lastX = 0;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      int action = motionEvent.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN: {
          GrabberView.this.pressed = true;
          GrabberView.this.scrollView.setDisableIntercept(true);
          lastX = motionEvent.getRawX();
          dX = GrabberView.this.getX() - motionEvent.getRawX();
          GrabberView.this.resetLinePaint(Color.BLACK);
          if( screenGuideView != null) {
            screenGuideView.fade(0, 1);
          }
          return true;
        }
        case MotionEvent.ACTION_MOVE: {
          float x = motionEvent.getRawX() + dX;
          float motionDx = motionEvent.getRawX() - lastX;
          GrabberView.this.setTranslationX(x);

          motionDx = Math.round(motionDx);
          dataProvider.updateWidth(motionDx, GrabberView.this.column);

          GrabberView.this.updateHeader(motionDx);
          lastX = motionEvent.getRawX();
          if(column == dataProvider.dataColumns.size() - 1 && motionDx > 0) {
            GrabberView.this.scrollView.updateLayout();
          }
          return true;
        }
        case MotionEvent.ACTION_UP: {
          GrabberView.this.pressed = false;
          GrabberView.this.resetLinePaint(TableTheme.borderBackgroundColor);
          GrabberView.this.scrollView.setDisableIntercept(false);
          GrabberView.this.scrollView.updateLayout();
          GrabberView.this.recyclerView.requestLayout();
          if( screenGuideView != null) {
            screenGuideView.fade(1, 0);
          }
          return  true;
        }
      }
      return false;
    }
  }
  public class GrabberButton extends View {
    public GrabberButton(View parent) {
      super(parent.getContext());
    }
  }

  public GrabberView(int column, Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.column = column;
    this.scrollView = scrollView;
    grabberButton = new GrabberButton(this);
    grabberButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight));
    this.addView(grabberButton);
    grabberButton.setOnTouchListener(new TouchListener());
    linePaint.setColor(TableTheme.borderBackgroundColor);
    linePaint.setStrokeWidth(PixelUtils.dpToPx(1));
  }

  public void resetLinePaint(int color) {
    linePaint = new Paint();
    linePaint.setColor(color);
    linePaint.setStrokeWidth(PixelUtils.dpToPx(1));
    this.invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (isLastColumn && !pressed) {
      return;
    }
    int width = canvas.getWidth() / 2;
    int top = isLastColumn && !pressed ? (TableTheme.headerHeight) : 0;
    canvas.drawLine(width, top, width, canvas.getHeight() - top, linePaint);
  }

  public void setDataProvider(DataProvider provider) {
    this.dataProvider = provider;
  }

  public void setHeaderView(AutoLinearLayout headerView) {
    this.headerView = headerView;
  }

  public void setFooterView(AutoLinearLayout footerView) {
    this.footerView = footerView;
  }

  public void setGreenGuideView(ScreenGuideView screenGuideView) {
    this.screenGuideView = screenGuideView;
  }

  public void setGrabbers(List<GrabberView> grabbers) {
    this.grabbers = grabbers;
    isLastColumn = (column == grabbers.size() - 1);
  }

  public void setRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
  }

  public void updateHeader(float dxMotion) {
    View view = headerView.getChildAt(column);
    resizeVew(view, dxMotion);
    updateNeighbour(headerView, dxMotion);

    updateFooter(dxMotion);
  }

  public void updateNeighbour(AutoLinearLayout linearLayout, float dxMotion) {
    if(column + 1 < linearLayout.getChildCount()) {
      View neighbour = linearLayout.getChildAt(column + 1);
      resizeVew(neighbour, -dxMotion);
    }
  }

  public void updateFooter(float dxMotion) {
    View view = footerView.getChildAt(column);
    resizeVew(view, dxMotion);
    updateNeighbour(footerView, dxMotion);

  }

  public void resizeVew(View view, float dxMotion) {
    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
    layoutParams.width += dxMotion;
    view.setLayoutParams(layoutParams);
  }
}
