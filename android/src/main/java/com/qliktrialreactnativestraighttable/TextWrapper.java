package com.qliktrialreactnativestraighttable;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;

public class TextWrapper {
  final TableView tableView;
  final TextView textView;
  DataColumn column;
  TextPaint measureTextPaint = new TextPaint();
  int lineCount = 1;
  int wordCount = 0;
  int additionalPadding = 0;


  TextWrapper(DataColumn column, TableView tableView, TextView textView) {
    this.column = column;
    this.tableView = tableView;
    this.textView = textView;

    measureTextPaint.setTextSize(textView.getTextSize());
    measureTextPaint.setAntiAlias(true);
  }

  TextWrapper(TableView tableView, TextView textView) {
    this.tableView = tableView;
    this.textView = textView;

    measureTextPaint.setTextSize(textView.getTextSize());
    measureTextPaint.setAntiAlias(true);
  }

  public void testTextWrap() {
    measureLineCount();
  }

  public void testTextWrap(DataColumn dataColumn) {
    this.column = dataColumn;
    measureLineCount();
  }

  public void testOnlyTextWrap() {
    measureLineCountNoUpdate();
  }
  void measureLineCount() {
    if (wordCount > 1) {
      int lines = calculateLineCount();
      if (lines != lineCount) {
        lineCount = Math.min(lines, wordCount);
        tableView.updateHeaderViewLineCount();
      }
    }
  }

  void measureLineCountNoUpdate() {
    if (wordCount > 1) {
      int lines = calculateLineCount();
      if (lines != lineCount) {
        lineCount = Math.min(lines, wordCount);
      }
    }
  }

  protected int calculateLineCount() {
    int width = Math.max(textView.getMeasuredWidth() - textView.getPaddingRight() - textView.getPaddingLeft(), 0);
    measureTextPaint.setTypeface(textView.getTypeface());
    StaticLayout.Builder builder = StaticLayout.Builder.obtain(textView.getText(), 0, textView.getText().length(), measureTextPaint, width);
    builder.setIncludePad(true);
    builder.setMaxLines(wordCount);
    builder.setAlignment(Layout.Alignment.ALIGN_NORMAL);
    builder.setLineSpacing(1, 1);
    StaticLayout layout = builder.build();
    int lines = layout.getLineCount();
    return lines;
  }

  public int getMeasuredLinedCount() {
    return lineCount;
  }

  public int setMaxLines(int maxLines, DataColumn column) {
    if(column.isDim) {
      countWords(textView.getText().toString());
      maxLines = wordCount > 1 ? maxLines : 1;
    } else {
      maxLines = 1;
      wordCount = 1;
    }
    return maxLines;

  }

  public void countWords(String text) {
    if(text != null) {
      wordCount = text.split("\\s+").length;
    }
  }
}
