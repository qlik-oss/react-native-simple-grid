package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

@SuppressLint("ViewConstructor")
public class HeaderCell extends LinearLayout {
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

    LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    layoutParams.gravity = Gravity.LEFT;
    layoutParams.weight = column.width;
    cell.setLayoutParams(layoutParams);
    addView(cell);

    if(column.isDim) {
      LinearLayout.LayoutParams searchLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
      searchLayoutParams.gravity = Gravity.RIGHT;
      searchLayoutParams.weight = 0;
      searchButton.setLayoutParams(searchLayoutParams);
      addView(searchButton);
    }
  }

  public void setColumn(DataColumn column) {
    this.cell.setColumn(column);
    setBackgroundColor(TableTheme.headerBackgroundColor);
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


  @SuppressLint("ViewConstructor")
  public class HeaderText extends androidx.appcompat.widget.AppCompatTextView {
    DataColumn column;
    TableView tableView;
    TextWrapper textWrapper;

    public HeaderText(Context context, DataColumn column, TableView tableView) {
      super(context);
      this.column = column;
      this.setCompoundDrawablePadding((int) PixelUtils.dpToPx(4));
      this.tableView = tableView;
      this.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
      textWrapper = new TextWrapper(column, tableView, this);
      this.setGravity(Gravity.CENTER_VERTICAL);

      updateArrow();
    }

    public void setColumn(DataColumn column) {
      this.column = column;
      textWrapper.column = column;
      setBackgroundColor(TableTheme.headerBackgroundColor);
      updateArrow();
    }

    private void updateArrow() {
      if (column.active) {
        if (column.sortDirection == null || column.sortDirection.compareToIgnoreCase("desc") == 0) {
          setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_up_24, 0, 0, 0);
        } else {
          setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_arrow_drop_down_24, 0, 0, 0);
        }
      } else {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      }
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

}
