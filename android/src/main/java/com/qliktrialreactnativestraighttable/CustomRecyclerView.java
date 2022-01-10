package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

public class CustomRecyclerView extends RecyclerView {

  public CustomRecyclerView(Context context) {
    super(context);
  }

  @Override
  public void requestLayout() {
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
