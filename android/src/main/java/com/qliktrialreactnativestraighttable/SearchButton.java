package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

@SuppressLint("ViewConstructor")
public class SearchButton extends androidx.appcompat.widget.AppCompatImageButton {
  final Drawable icon;
  final Context context;
  final DataColumn column;
  final TableView tableView;
  final int defaultColor = Color.TRANSPARENT;

  public SearchButton(Context context, TableView tableView, DataColumn column) {
    super(context);
    this.context = context;
    this.tableView = tableView;
    this.column = column;
    this.icon = context.getDrawable(R.drawable.ic_searchicon);
    this.setImageDrawable(icon);
    this.setBackgroundColor(defaultColor);
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();

    if (action == MotionEvent.ACTION_UP) {
      EventUtils.sendOnSearchColumn(tableView, column);
      postInvalidate();
    }
    return super.onTouchEvent(event);
  }
}
