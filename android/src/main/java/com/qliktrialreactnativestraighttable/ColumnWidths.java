package com.qliktrialreactnativestraighttable;

import java.util.ArrayList;
import java.util.List;

public class ColumnWidths {
  List<DataColumn> dataColumns;
  protected List<Integer> widths = new ArrayList<>();

  public ColumnWidths () {

  }

  public void loadWidths(int frameWidth, List<DataColumn> dataColumns) {
    this.dataColumns = dataColumns;
    widths.clear();
    int defaultWidth = (int)PixelUtils.dpToPx(200);//frameWidth / dataColumns.size();
    for(DataColumn col : dataColumns) {
      col.width = defaultWidth;
      widths.add(defaultWidth);
    }
  }

  public void updateWidths(List<DataColumn> columns) {
    if(dataColumns == null) {
      return;
    }
    if(dataColumns.size() == widths.size()) {
      dataColumns = columns;
      for (int i = 0; i < widths.size(); i++) {
        DataColumn column = dataColumns.get(i);
        column.width = widths.get(i);
      }
    }
  }

  public void updateWidths() {
    if(dataColumns.size() == widths.size()) {
      for (int i = 0; i < dataColumns.size(); i++) {
        widths.set(i, dataColumns.get(i).width);
      }
    }
  }

  public List<Integer> getWidths() {
    return widths;
  }

  public void updateWidth(int index, int delta) {
    int width = widths.get(index);
    width += delta;
    widths.set(index, width);
  }

  public int getWidth(int index) {
    return widths.get(index);
  }

  public int getTotalWidth() {
    return widths.stream().reduce(0, (a, b) -> a + b);
  }
}
