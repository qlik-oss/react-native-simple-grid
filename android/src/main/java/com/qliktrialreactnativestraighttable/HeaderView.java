package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import java.util.List;


public class HeaderView extends AutoLinearLayout{
  final TableView tableView;
  List<DataColumn> dataColumns = null;
  HeaderView(Context context, TableView tableView) {
    super(context);
    this.tableView = tableView;
  }

  public void setDataColumns(List<DataColumn> dataColumns) {
    this.dataColumns = dataColumns;
  }

  public void update(List<DataColumn> dataColumns) {
    this.dataColumns = dataColumns;
    for(int i = 0; i < this.dataColumns.size(); i++) {
      DataColumn column = this.dataColumns.get(i);
      HeaderCell headerCell = (HeaderCell) this.getChildAt(i);
      headerCell.setColumn(column);
    }
  }

  public void updateLayout() {
    for (int i = 0; i < this.dataColumns.size(); i++){
      HeaderCell headerCell = (HeaderCell)this.getChildAt(i);
      ViewGroup.LayoutParams layoutParams = headerCell.getLayoutParams();
      layoutParams.width = dataColumns.get(i).width;
      headerCell.setLayoutParams(layoutParams);
    }
  }

  public void testTextWrap() {
    for (int i = 0; i < this.dataColumns.size(); i++) {
      HeaderCell headerCell = (HeaderCell) this.getChildAt(i);
      headerCell.cell.testTextWrap();
    }
  }

  public int getMaxLineCount() {
    int lineCount = 0;
    for(int i = 0; i < this.dataColumns.size(); i++) {
      HeaderCell headerCell = (HeaderCell)this.getChildAt(i);
      lineCount = Math.max(lineCount, headerCell.cell.getMeasuredLinedCount());
    }

    for(int i = 0; i < this.dataColumns.size(); i++) {
      HeaderCell headerCell = (HeaderCell)this.getChildAt(i);
      headerCell.cell.setMaxLines(lineCount, dataColumns.get(i));
    }
    return lineCount;
  }
}
