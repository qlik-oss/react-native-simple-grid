package com.qliktrialreactnativestraighttable;

import android.widget.TextView;

public class ClickableTextWrapper extends TextWrapper{
  ClickableTextWrapper(TableView tableView, TextView textView) {
    super(tableView, textView);
  }

  @Override
  void measureLineCount() {
    if(wordCount > 1) {
      int lines = calculateLineCount();
      if(lines != lineCount && lines <= wordCount) {
        lineCount = lines;
        tableView.updateRecyclerViewLineCount(column);
      }
    }
  }
  int getMeasureLinedCount(DataColumn column) {
    this.column = column;
    if(wordCount > 1) {
      return calculateLineCount();
    }
    return 1;
  }
}
