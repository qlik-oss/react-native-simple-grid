package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CustomHorizontalScrollView extends HorizontalScrollView {
  MockHorizontalScrollView horizontalScrollBar;
  boolean disableIntercept = false;
  public CustomHorizontalScrollView(Context context) {
    super(context);
    setHorizontalScrollBarEnabled(false);
  }

  @Override
  protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    super.onScrollChanged(l, t, oldl, oldt);
    if (horizontalScrollBar != null) {
      horizontalScrollBar.setContentWidth(computeHorizontalScrollRange());
      horizontalScrollBar.setScrollX(computeHorizontalScrollOffset());
    }
  }

  public void setScrollbar(MockHorizontalScrollView verticalScrollBar){
    this.horizontalScrollBar = verticalScrollBar;
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
