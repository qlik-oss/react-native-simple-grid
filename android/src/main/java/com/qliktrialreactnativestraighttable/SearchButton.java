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
  final int pressedColor = Color.parseColor("#595959");

  public SearchButton(Context context, TableView tableView, DataColumn column) {
    super(context);
    this.context = context;
    this.tableView = tableView;
    this.column = column;
    this.icon = context.getDrawable(R.drawable.ic_searchicon);
    this.setImageDrawable(icon);
    this.setBackgroundColor(defaultColor);
  }

  public void handleTouchDown(){
    this.setBackgroundColor(pressedColor);

    icon.setTint(Color.WHITE);
    this.setImageDrawable(icon);
    EventUtils.sendOnSearchColumn(tableView, column);
    postInvalidate();
  }

  public void handleTouchUp(){
    this.setBackgroundColor(defaultColor);

    icon.setTint(Color.BLACK);
    this.setImageDrawable(icon);
    EventUtils.sendOnSearchColumn(tableView, column);
    postInvalidate();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    int action = event.getAction();

    switch(action) {
      case MotionEvent.ACTION_DOWN:
        handleTouchDown();
        break;
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_HOVER_EXIT:
        handleTouchUp();
        break;
    }
    return super.onTouchEvent(event);
  }
}
