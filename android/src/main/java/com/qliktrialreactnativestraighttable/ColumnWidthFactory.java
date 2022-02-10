package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ColumnWidthFactory {
  final List<DataColumn> columnList;
  final List<DataRow> rowList;
  final Context parentContext;
  final HeaderView headerView;
  final CustomHorizontalScrollView scrollView;

  ColumnWidthFactory(List<DataColumn> columnList, List<DataRow> rowList, Context parentContext, HeaderView headerView, CustomHorizontalScrollView scrollView) {
    this.columnList = columnList;
    this.rowList = rowList;
    this.parentContext = parentContext;
    this.headerView = headerView;
    this.scrollView = scrollView;
  }

  public void autoSize(CustomHorizontalScrollView contextView) {
    boolean resized = false;
    for(int columnIndex = 0; columnIndex < columnList.size(); columnIndex++ ) {
      DataColumn column = columnList.get(columnIndex);
      if (column.width == 0) {
        column.width = resizeColumnByAverage(columnIndex);
        resized = true;
      }
    }

    if (resized) {
      requestLayoutHeaderView();
      EventUtils.sendOnColumnResize(contextView, this.columnList);
    }
  }

  private int resizeColumnByAverage(int columnIndex) {
    int runningTotal = 0;
    TextView tempText = new TextView(parentContext);
    for(DataRow row : rowList) {
      if (row != null) {
        runningTotal += row.cells.get(columnIndex).qText.length();
      }
    }
    int averageTextSize = runningTotal / rowList.size();
    // Create a string with max text
    String tempString = new String(new char[averageTextSize]).replace("\0", "X");

    int width = Math.max((int)tempText.getPaint().measureText(tempString), 50);
    return (int)PixelUtils.dpToPx(width);
  }

  private void requestLayoutHeaderView() {
    if (this.headerView != null) {
     HeaderViewFactory headerViewFactory = new HeaderViewFactory(this.headerView, this.scrollView);
     headerViewFactory.readjustLayout(columnList, this.parentContext);
    }
  }
}
