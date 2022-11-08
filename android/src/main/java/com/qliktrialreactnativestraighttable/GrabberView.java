package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GrabberView extends LinearLayout {
  GrabberButton grabberButton;
  Paint linePaint = new Paint();
  final CustomHorizontalScrollView scrollView;
  final TableView tableView;
  DataProvider dataProvider = null;
  AutoLinearLayout headerView = null;
  AutoLinearLayout footerView = null;
  AutoLinearLayout totalsView = null;
  List<GrabberView> grabbers = null;
  CustomRecyclerView recyclerView;
  CustomRecyclerView firstColumnRecyclerView;
  HeaderCell firstColumnHeader;
  TextView fixedTotalsCell;
  ScreenGuideView screenGuideView = null;
  RootLayout rootLayout = null;
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
          tableView.hideDragBoxes();
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
          motionDx = (float) Math.round(motionDx);
          if (dataProvider.updateWidth(motionDx, GrabberView.this.column)) {
            // cast here to avoid drift
            GrabberView.this.setTranslationX((int)x);
            GrabberView.this.updateHeader(motionDx);
            GrabberView.this.updateFixedTotalsCell(motionDx);
            GrabberView.this.updateFirstColumnHeader(motionDx);
            GrabberView.this.updateTotals(motionDx);
            lastX = motionEvent.getRawX();
            if(isLastColumn && motionDx > 0) {
              GrabberView.this.rootLayout.requestLayout();
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
          GrabberView.this.rootLayout.requestLayout();
          if(GrabberView.this.firstColumnRecyclerView != null) {
            firstColumnRecyclerView.requestLayout();
          }
          GrabberView.this.dataProvider.onEndPan();
          GrabberView.this.postInvalidate();
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

  @SuppressLint("ClickableViewAccessibility")
  public GrabberView(int column, Context context, CustomHorizontalScrollView scrollView, TableView tableView) {
    super(context);
    this.column = column;
    this.scrollView = scrollView;
    this.tableView = tableView;
    grabberButton = new GrabberButton(this);
    grabberButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.rowHeightFactor));
    this.addView(grabberButton);
    grabberButton.setBackgroundColor(Color.TRANSPARENT);
    grabberButton.setOnTouchListener(new TouchListener());
    linePaint.setColor(TableTheme.borderBackgroundColor);
    linePaint.setStrokeWidth(PixelUtils.dpToPx(1));
  }

  public void setHeaderHeight(int height) {
    if (grabberButton != null) {
      ViewGroup.LayoutParams params = grabberButton.getLayoutParams();
      params.height = height;
      grabberButton.setLayoutParams(params);
    }
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
    int width = getWidth() / 2;
    int top = 0;
    int height = getHeight() - top;
    canvas.drawLine(width, top, width, height - TableTheme.rowHeightFactor, linePaint);
  }

  public void setFirstColumnHeader(HeaderCell cell) {
    this.firstColumnHeader = cell;
  }

  public void setFixedTotalsCell(TextView cell) {
    this.fixedTotalsCell = cell;
  }

  public void setFirstColumnRecyclerView(CustomRecyclerView view) {
    this.firstColumnRecyclerView = view;
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
    HeaderCell headerCell = (HeaderCell)headerView.getChildAt(column);
    resizeView(headerCell, dxMotion);
    headerCell.cell.testTextWrap();
    View neighbour = updateNeighbour(headerView, dxMotion);
    if(neighbour != null) {
      headerCell = (HeaderCell) neighbour;
      headerCell.cell.testTextWrap();
    }
  }

  public void updateFixedTotalsCell(float dxMotion) {
    if(fixedTotalsCell != null && column == 0) {
      resizeView(fixedTotalsCell, dxMotion);
    }
  }

  public void updateTotals(float dxMotion) {
    if(totalsView != null) {
      View view = totalsView.getChildAt(column);
      resizeView(view, dxMotion);
      updateNeighbour(totalsView, dxMotion);
    }
  }

  public void updateFirstColumnHeader(float dxMotion) {
    if(firstColumnHeader != null && column == 0) {
      resizeView(firstColumnHeader, dxMotion);
    }
  }

  public View updateNeighbour(LinearLayout container, float dxMotion) {
    if(column + 1 < container.getChildCount()) {
      View neighbour = container.getChildAt(column + 1);
      resizeView(neighbour, -dxMotion);
      return neighbour;
    }
    return null;
  }

  public void resizeView(View view, float dxMotion) {
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    layoutParams.width += dxMotion;
    view.setLayoutParams(layoutParams);
  }

  public void updateLayout() {
    this.setElevation(PixelUtils.dpToPx(5));
  }
}
