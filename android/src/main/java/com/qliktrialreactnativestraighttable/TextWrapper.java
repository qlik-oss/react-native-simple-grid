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

  void measureLineCount() {
    if (wordCount > 1) {
      int lines = calculateLineCount();
      if (lines != lineCount) {
        lineCount = Math.min(lines, wordCount);
        tableView.updateHeaderViewLineCount();
      }
    }
  }

  protected int calculateLineCount() {
    int width = column.width - textView.getPaddingLeft() - textView.getPaddingRight() - additionalPadding;
    measureTextPaint.setTypeface(textView.getTypeface());
    StaticLayout.Builder builder = StaticLayout.Builder.obtain(textView.getText(), 0, textView.getText().length(), measureTextPaint, width);
    builder.setIncludePad(true);

    builder.setAlignment(Layout.Alignment.ALIGN_NORMAL);
    builder.setLineSpacing(1, 1);
    StaticLayout layout = builder.build();
    int lines = layout.getLineCount();
    return lines;
  }

  public int getMeasuredLinedCount() {
    return lineCount;
  }

  public int setMaxLines(int maxLines) {
    countWords(textView.getText().toString());
    maxLines = wordCount > 1 ? maxLines : 1;
    return maxLines;
  }

  public void countWords(String text) {
    if(text != null) {
      wordCount = text.split("\\s+").length;
    }
  }
}