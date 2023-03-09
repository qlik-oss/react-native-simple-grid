package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class RootLayout extends FrameLayout {
  ColumnWidths columnWidths;

  RootLayout(Context context) {
    super(context);
  }

  public void setColumnWidths(ColumnWidths columnWidths) {
    this.columnWidths = columnWidths;
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
        MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.UNSPECIFIED),
        MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
      int l = getLeft();
      int r = l + columnWidths.getTotalWidth() + getPaddingRight();
      layout(getLeft(), getTop(), r, getBottom());
    }
  };
}
