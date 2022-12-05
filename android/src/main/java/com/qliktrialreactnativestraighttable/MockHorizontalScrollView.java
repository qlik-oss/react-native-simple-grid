package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

@SuppressLint("ViewConstructor")
public class MockHorizontalScrollView extends HorizontalScrollView {
  View content;

  public MockHorizontalScrollView(Context context) {
    super(context);
    setHorizontalScrollBarEnabled(true);
    setZ(PixelUtils.dpToPx(4));

    content = new View(context);
    content.setBackgroundColor(Color.TRANSPARENT);
    content.setMinimumHeight((int) PixelUtils.dpToPx(6));
    addView(content);
  }

  public void setContentWidth(int width){
    content.setMinimumWidth(width);
  }
}
