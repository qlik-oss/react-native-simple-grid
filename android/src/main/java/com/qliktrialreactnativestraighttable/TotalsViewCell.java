package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

@SuppressLint("ViewConstructor")
public class TotalsViewCell extends androidx.appcompat.widget.AppCompatTextView {
  Paint paint = new Paint();
  DataColumn column;
  TextWrapper textWrapper;
  final TableView tableView;
  public TotalsViewCell(Context context, DataColumn dataColumn, TableView tableView) {
    super(context);
    this.column = dataColumn;
    this.tableView = tableView;
    textWrapper = new TextWrapper(column, tableView, this);
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
    paint.setColor(TableTheme.borderBackgroundColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(PixelUtils.dpToPx(2));

    canvas.drawLine(0, 0, column.width, 0, paint);
    canvas.drawLine(0, TableTheme.DefaultRowHeight, column.width, TableTheme.DefaultRowHeight, paint);
  }
}
