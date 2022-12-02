package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class TotalsViewCell extends androidx.appcompat.widget.AppCompatTextView {
  Paint paint = new Paint();
  DataColumn column;
  TextWrapper textWrapper;
  Rect bounds = new Rect();
  final TableView tableView;
  public TotalsViewCell(Context context, DataColumn dataColumn, TableView tableView) {
    super(context);
    this.column = dataColumn;
    this.tableView = tableView;
    textWrapper = new TextWrapper(column, tableView, this);

    int textColor = tableView.cellContentStyle.color;
    setTextColor(textColor);
  }

  public void setColumn(DataColumn col) {
    this.column = col;
  }

  public void testTextWrap() {
    if(tableView.headerContentStyle.wrap) {
      textWrapper.testOnlyTextWrap();
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

  protected void onDraw(Canvas canvas){
    super.onDraw(canvas);
    canvas.getClipBounds(bounds);
    paint.setColor(TableTheme.borderBackgroundColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(PixelUtils.dpToPx(2));

    canvas.drawLine(0, 0, column.width, 0, paint);
    canvas.drawLine(0, bounds.height(), column.width, bounds.height(), paint);
  }
}
