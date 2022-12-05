package com.qliktrialreactnativestraighttable;

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
  public CustomHorizontalScrollView(Context context) {
    super(context);
    setHorizontalScrollBarEnabled(false);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    int scrollRange = computeHorizontalScrollRange();
    int scrollX = computeHorizontalScrollOffset();

    if (horizontalScrollBar == null || verticalScrollBar == null) {
      return;
    }

    horizontalScrollBar.setContentWidth(scrollRange);
    horizontalScrollBar.setScrollX(scrollX);

    int overScroll = scrollX + horizontalScrollBar.getMeasuredWidth() - scrollRange + (int) PixelUtils.dpToPx(50);
    verticalScrollBar.setOverScrolled(Math.max(0, overScroll));
    verticalScrollBar.setTranslationX(-verticalScrollBar.overScrolled);

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
