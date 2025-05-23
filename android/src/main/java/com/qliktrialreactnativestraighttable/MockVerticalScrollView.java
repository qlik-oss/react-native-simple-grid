package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public class MockVerticalScrollView extends ScrollView {
  int overScrollOffset = 0;
  View content;

  public MockVerticalScrollView(Context context) {
    super(context);
    setVerticalScrollBarEnabled(true);
    setZ(PixelUtils.dpToPx(4));

    content = new View(context);
    content.setBackgroundColor(Color.TRANSPARENT);
    addView(content);
  }

  public void setOverScrollOffset(int offset) {
    overScrollOffset = offset;
    setTranslationX(-Math.max(0, offset));
  }

  public void setContentHeight(int height){
    content.setMinimumHeight(height);
  }
}
