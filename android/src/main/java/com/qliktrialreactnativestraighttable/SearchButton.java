package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;


public class SearchButton extends androidx.appcompat.widget.AppCompatImageButton {
  final Drawable icon;
  final Context context;
  DataColumn column;
  TableView tableView;
  final int defaultColor = Color.TRANSPARENT;

  public SearchButton(Context context) {
    super(context);
    this.context = context;
    this.icon = context.getDrawable(R.drawable.ic_searchicon);
    this.setImageDrawable(icon);
    this.setBackgroundColor(defaultColor);
  }

  public void setTableView(TableView tableView) {
    this.tableView = tableView;
  }

  public void setColumn(DataColumn column) {
    this.column = column;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();

    if (action == MotionEvent.ACTION_UP) {
      performClick();
      return true;
    }
    return super.onTouchEvent(event);
  }

  @Override
  public boolean performClick() {
    EventUtils.sendOnSearchColumn(tableView, column);
    postInvalidate();
    return super.performClick();
  }
}
