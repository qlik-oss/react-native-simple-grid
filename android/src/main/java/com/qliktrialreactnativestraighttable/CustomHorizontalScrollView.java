package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class CustomHorizontalScrollView extends HorizontalScrollView {
  MockHorizontalScrollView horizontalScrollBar;
  MockVerticalScrollView verticalScrollBar;

  boolean disableIntercept = false;
  public CustomHorizontalScrollView(Context context, TableView tableView) {
    super(context);
    setHorizontalScrollBarEnabled(false);
  }

  public int getOverScrollOffset() {
    int scrollRange = computeHorizontalScrollRange();
    int scrollX = computeHorizontalScrollOffset();
    return scrollX + getMeasuredWidth() - scrollRange + TableTheme.HorizontalScrollViewPadding;
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    int scrollRange = computeHorizontalScrollRange();
    int scrollX = computeHorizontalScrollOffset();

    if (horizontalScrollBar == null || verticalScrollBar == null) {
      return;
    }

    horizontalScrollBar.setContentWidth(scrollRange + TableTheme.HorizontalScrollViewPadding);
    horizontalScrollBar.setScrollX(scrollX);

    verticalScrollBar.setOverScrollOffset(getOverScrollOffset());
  }

  public void setScrollbars(MockHorizontalScrollView horizontalScrollBar, MockVerticalScrollView verticalScrollBar){
    this.horizontalScrollBar = horizontalScrollBar;
    this.verticalScrollBar = verticalScrollBar;
  }

  void setDisableIntercept(boolean value) {
    disableIntercept = value;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (disableIntercept) {
      return false;
    }
    return super.onInterceptTouchEvent(ev);
  }

  public void updateLayout() {
    super.requestLayout();
    post(measureAndLayout);
  }

  private final Runnable measureAndLayout = new Runnable() {
    @Override
    public void run() {
      measure(
        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
      layout(getLeft(), getTop(), getRight(), getBottom());
    }
  };
}
