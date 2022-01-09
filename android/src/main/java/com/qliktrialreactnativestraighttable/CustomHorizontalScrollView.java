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
}
