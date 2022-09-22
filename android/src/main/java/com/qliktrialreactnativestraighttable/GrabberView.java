package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import okhttp3.internal.http2.Header;

public class GrabberView extends LinearLayout {
  GrabberButton grabberButton;
  Paint linePaint = new Paint();
  CustomHorizontalScrollView scrollView;
  DataProvider dataProvider = null;
  AutoLinearLayout headerView = null;
  AutoLinearLayout footerView = null;
  List<GrabberView> grabbers = null;
  CustomRecyclerView recyclerView;
  final FrameLayout rootView;
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
          GrabberView.this.scrollView.requestDisallowInterceptTouchEvent(true);
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

          motionDx = Math.round(motionDx);
          if (dataProvider.updateWidth(motionDx, GrabberView.this.column)) {
            GrabberView.this.setTranslationX(x);
            GrabberView.this.updateHeader(motionDx);

            if(rootView != null) {
              HeaderCell firstColumnHeader = (HeaderCell) rootView.getChildAt(1);
              CustomRecyclerView firstColumnRecyclerView = (CustomRecyclerView) rootView.getChildAt(3);

              int headerHeight = firstColumnHeader.getMeasuredHeight();
              int rootHeight = rootView.getMeasuredHeight();
              firstColumnRecyclerView.layout(0, 0, dataProvider.dataColumns.get(0).width - 5, rootHeight - TableView.SCROLL_THUMB_HEIGHT);
              firstColumnHeader.layout(0, 0, dataProvider.dataColumns.get(0).width, headerHeight);
              firstColumnHeader.setEllipsize(TextUtils.TruncateAt.END);
            }


            lastX = motionEvent.getRawX();
            if(column == dataProvider.dataColumns.size() - 1 && motionDx > 0) {
              GrabberView.this.recyclerView.requestLayout();
              GrabberView.this.scrollView.updateLayout();
              GrabberView.this.scrollView.scrollBy((int) motionDx, 0);
            }
          }
          return true;
        }
        case MotionEvent.ACTION_UP: {
          GrabberView.this.pressed = false;
          GrabberView.this.resetLinePaint(TableTheme.borderBackgroundColor);
          GrabberView.this.scrollView.setDisableIntercept(false);
          GrabberView.this.scrollView.requestDisallowInterceptTouchEvent(false);
          GrabberView.this.scrollView.updateLayout();
          GrabberView.this.recyclerView.requestLayout();
          GrabberView.this.dataProvider.onEndPan(scrollView);
          if(screenGuideView != null) {
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

  public GrabberView(int column, Context context, CustomHorizontalScrollView scrollView, FrameLayout rootView) {
    super(context);
    this.column = column;
    this.scrollView = scrollView;
    this.rootView = rootView;
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
    this.recyclerView = (CustomRecyclerView) recyclerView;
  }

  public void updateHeader(float dxMotion) {
    if(column == 0 && rootView != null) {
      View view = rootView.getChildAt(column);
      resizeView(view, dxMotion);
      updateChildren(rootView, dxMotion);
    }
    View view = headerView.getChildAt(column);
    resizeView(view, dxMotion);
    updateNeighbour(headerView, dxMotion);
    updateFooter(dxMotion);
  }

  public void updateChildren(ViewGroup container, float dxMotion) {
    for(int i = 0; i < container.getChildCount(); i++) {
      View neighbour = container.getChildAt(i);
      resizeView(neighbour, -dxMotion);
    }
  }

  public void updateNeighbour(LinearLayout container, float dxMotion) {
    if(column + 1 < container.getChildCount()) {
      View neighbour = container.getChildAt(column + 1);
      resizeView(neighbour, -dxMotion);
    }
  }

  public void updateFooter(float dxMotion) {
    if (footerView != null) {
      View view = footerView.getChildAt(column);
      resizeView(view, dxMotion);
      updateNeighbour(footerView, dxMotion);
    }
  }

  public void resizeView(View view, float dxMotion) {
    ViewGroup.LayoutParams layoutParams = (ViewGroup.LayoutParams) view.getLayoutParams();
    layoutParams.width += dxMotion;
    view.setLayoutParams(layoutParams);
  }
}
