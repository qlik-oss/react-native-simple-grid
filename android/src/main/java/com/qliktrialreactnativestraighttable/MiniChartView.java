package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.View;

public class MiniChartView extends View implements Content {
  Rect bounds = new Rect();
  Paint paint = new Paint();
  DataCell dataCell = null;
  MiniChartRenderer miniChartRenderer = null;

  public MiniChartView(Context context) {
    super(context);
    paint.setColor(Color.BLUE);
  }

  public void setData(DataCell cell, DataColumn column) {
    if(miniChartRenderer == null) {
      if (cell.miniChart != null && column.representation != null) {
        if (column.representation.miniChart != null) {
          if (column.representation.miniChart.type.equals("bars")) {
            miniChartRenderer = new MiniBarChartRenderer(cell.miniChart, column.representation);
          } else if(column.representation.miniChart.type.equals("dots")) {
            miniChartRenderer = new MiniDotsChartRenderer(cell.miniChart, column.representation);
          } else if(column.representation.miniChart.type.equals("sparkline")) {
            miniChartRenderer = new MiniSparkLinesRenderer(cell.miniChart, column.representation);
          } else if(column.representation.miniChart.type.equals("posNeg")) {
            miniChartRenderer = new MiniPosNegRenderer(cell.miniChart, column.representation);
          }
        }
      }
    } else {
      miniChartRenderer.updateData(cell.miniChart, column.representation);
    }
    this.invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if(miniChartRenderer != null){
      canvas.getClipBounds(bounds);
      miniChartRenderer.resetScales(bounds);
      miniChartRenderer.render(canvas);
    }
  }

  @Override
  public void updateBackgroundColor() {
    // no selections
  }

  @Override
  public void setGestureDetector(GestureDetector gestureDetector) {

  }

  @Override
  public void toggleSelected() {
    // no selections
  }

  @Override
  public void setCell(DataCell cell) {
    dataCell = cell;
  }

  @Override
  public DataCell getCell() {
    return dataCell;
  }
}
