package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

@SuppressLint("ViewConstructor")
public class HeaderCell extends LinearLayout {
  String sortIndicatorState = "none";
  Paint paint = new Paint();
  DataColumn column;
  TableView tableView;
  HeaderText cell;
  SearchButton searchButton;

  public HeaderCell(Context context, DataColumn column, TableView tableView) {
    super(context);
    this.column = column;
    this.tableView = tableView;
    this.cell = new HeaderText(context, column, tableView);
    this.searchButton = new SearchButton(context, tableView, column);

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutParams.weight = 1;
    cell.setLayoutParams(layoutParams);
    addView(cell);

    if(column.isDim) {
      LinearLayout.LayoutParams searchLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      searchLayoutParams.weight = 0;
      searchButton.setLayoutParams(searchLayoutParams);
      addView(searchButton);
    }
  }

  public void setColumn(DataColumn column) {
    sortIndicatorState = "none";
    this.column = column;
    setBackgroundColor(TableTheme.headerBackgroundColor);
    cell.setBackgroundColor(Color.TRANSPARENT);
    if(column != null && column.label != null) {
      cell.setText(column.label);
    }
    if (column.active) {
      if (column.sortDirection == null || column.sortDirection.compareToIgnoreCase("desc") == 0) {
        sortIndicatorState = "top";
      } else {
        sortIndicatorState = "bottom";
      }
    }
    postInvalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint.setStrokeWidth(PixelUtils.dpToPx(6));
    paint.setColor(Color.BLACK);
    switch(sortIndicatorState) {
      case "top":
        canvas.drawLine(0, getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight(), paint);
        break;
      case "bottom":
        canvas.drawLine(0, 0, getMeasuredWidth(), 0, paint);
        break;
      default:
      case "none":
        break;
    }
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
    setBackgroundColor(Color.LTGRAY);
    cell.setBackgroundColor(Color.TRANSPARENT);
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

  void setMaxLines(int maxLineCount) {
    if(this.cell != null && this.column != null) {
      this.cell.setMaxLines(maxLineCount, this.column);
    }
  }
  @SuppressLint("ViewConstructor")
  public class HeaderText extends androidx.appcompat.widget.AppCompatTextView {
    DataColumn column;
    TableView tableView;
    TextWrapper textWrapper;

    public HeaderText(Context context, DataColumn column, TableView tableView) {
      super(context);
      this.column = column;
      this.tableView = tableView;
      this.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
      textWrapper = new TextWrapper(column, tableView, this);
      if(column.isDim) {
        textWrapper.additionalPadding = TableTheme.CellPadding * 2;
      }
      this.setBackgroundColor(Color.TRANSPARENT);
      this.setGravity(Gravity.CENTER_VERTICAL);
    }

    public void testTextWrap() {
      if(tableView.headerContentStyle.wrap) {
        textWrapper.testTextWrap();
      }
    }

    public void setMaxLines(int maxLines, DataColumn column) {
      maxLines = textWrapper.setMaxLines(maxLines, column);
      super.setMaxLines(maxLines);
    }

    int getMeasuredLinedCount() {
      return textWrapper.getMeasuredLinedCount();
    }
  }

}
