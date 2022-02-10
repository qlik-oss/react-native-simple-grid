package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;


public class HeaderCell extends androidx.appcompat.widget.AppCompatTextView {
  DataColumn column;
  CustomHorizontalScrollView scrollView;
  public HeaderCell(Context context, DataColumn column, CustomHorizontalScrollView scrollView) {
    super(context);
    this.column = column;
    this.setCompoundDrawablePadding((int)PixelUtils.dpToPx(4));
    this.scrollView = scrollView;
    updateArrow();
  }

  public void setColumn(DataColumn column) {
    this.column = column;
    setBackgroundColor(Color.TRANSPARENT);
    updateArrow();
  }

  private void updateArrow() {
    if (column.active) {
      if(column.sortDirection.compareToIgnoreCase("desc") == 0) {
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_up_24, 0, 0, 0);
      } else {
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_down_24, 0, 0, 0);
      }
    } else {
      setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }
  }

  public void handleSingleTap() {
    EventUtils.sendOnHeaderTapped(scrollView, column);
  }

  public void handleDown() {
    this.setBackgroundColor(Color.LTGRAY);
    this.invalidate();
  }

  @Override
  public boolean onTouchEvent(MotionEvent e) {
    switch (e.getAction()) {
      case MotionEvent.ACTION_DOWN:
        handleDown();
        return true;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        handleSingleTap();
        return true;
      default:
        break;
    }
    return false;
  }

}
