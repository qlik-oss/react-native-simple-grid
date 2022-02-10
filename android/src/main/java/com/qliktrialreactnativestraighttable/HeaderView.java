package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;

import java.util.List;

public class HeaderView extends AutoLinearLayout{
  List<DataColumn> dataColumns = null;
  HeaderView(Context context) {
    super(context);
  }

  public void setDatColumns(List<DataColumn> dataColumns) {
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
}
