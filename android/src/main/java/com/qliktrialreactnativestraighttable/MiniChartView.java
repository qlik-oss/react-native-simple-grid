package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class MiniChartView extends View {
  Rect bounds = new Rect();
  Paint paint = new Paint();
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
          }
        }
      }
    } else {
      miniChartRenderer.updateData(cell.miniChart);
    }
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    if(miniChartRenderer != null) {
      bounds.set(l, t, r, b);
      miniChartRenderer.resetScales(bounds);
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if(miniChartRenderer != null){
      miniChartRenderer.render(canvas);
    }
  }
}
