package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import androidx.annotation.NonNull;

@SuppressLint("ViewConstructor")
public class HeaderCell extends androidx.appcompat.widget.AppCompatTextView {
  DataColumn column;
  TableView tableView;
  TextWrapper textWrapper;

  public HeaderCell(Context context, DataColumn column, TableView tableView) {
    super(context);
    this.column = column;
    this.setCompoundDrawablePadding((int) PixelUtils.dpToPx(4));
    this.tableView = tableView;
    textWrapper = new TextWrapper(column, tableView, this);

    updateArrow();
  }

  public void setColumn(DataColumn column) {
    this.column = column;
    textWrapper.column = column;
    setBackgroundColor(TableTheme.headerBackgroundColor);
    updateArrow();
  }

  private void updateArrow() {
//    if (column.active) {
//      if (column.sortDirection == null || column.sortDirection.compareToIgnoreCase("desc") == 0) {
//        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_up_24, 0, 0, 0);
//      } else {
//        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_down_24, 0, 0, 0);
//      }
//    } else {
//      setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//    }
  }

  public void handleSingleTap() {
    if (column.sortDirection == null && column.active) {
      return;
    }
    EventUtils.sendOnHeaderTapped(tableView, column);

  }

  public void handleDown() {
    if (column.sortDirection == null && column.active) {
      return;
    }
    this.setBackgroundColor(Color.LTGRAY);
    this.invalidate();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(@NonNull MotionEvent e) {
    boolean result = false;
    switch (e.getAction()) {
      case MotionEvent.ACTION_DOWN:
        handleDown();
        result = true;
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        handleSingleTap();
        result = true;
        break;
      default:
        break;
    }
    return result;
  }

  public void testTextWrap() {
    if(tableView.headerContentStyle.wrap) {
      textWrapper.testTextWrap();
    }
  }

  @Override
  public void setMaxLines(int maxLines) {
    maxLines = textWrapper.setMaxLines(maxLines);
    super.setMaxLines(maxLines);
  }

  int getMeasuredLinedCount() {
    return textWrapper.getMeasuredLinedCount();
  }


}
