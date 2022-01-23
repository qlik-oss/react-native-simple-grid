package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class CustomHorizontalScrollView extends HorizontalScrollView {
  boolean disableIntercept = false;
  public CustomHorizontalScrollView(Context context) {
    super(context);
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
