package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class MiniDotsChartRenderer extends MiniChartRenderer{
  float dotRadius = 8.0f;
  public MiniDotsChartRenderer(qMiniChart chartData, Representation representation) {
    super(chartData, representation);
  }

  @Override
  public void render(Canvas canvas) {
    dotRadius = Math.max(Math.min(8.0f, bandwidth / 2.0f), 2.0f);
    float x = padding + (horizontalPadding / 2.0f);
    paint.setColor(miniChartInfo.colors.main.color);
    for(int i = 0; i < miniChartData.matrix.rows.size(); i++) {
      float value = (float) miniChartData.matrix.rows.get(i).columns.get(1).qNum;
      float height = value * scale;
      float y = xAxis - height  ;
      setColor(i, value, miniChartData.matrix.rows.size());
      canvas.drawCircle(x, y, dotRadius, paint);
      x += padding * 2.0f + bandwidth;
    }
  }

  public void renderColorValues(Canvas canvas) {
    dotRadius = Math.max(Math.min(8.0f, bandwidth / 2.0f), 2.0f);
    float x = padding + (horizontalPadding / 2.0f);
    paint.setColor(miniChartInfo.colors.main.color);
    for(int i = 0; i < miniChartData.matrix.rows.size(); i++) {
      float value = (float) miniChartData.matrix.rows.get(i).columns.get(1).qNum;
      float height = value * scale;
      float y = xAxis - height  ;
      if(miniChartInfo.colors.max.valid && value == (float) miniChartData.qMax) {
        paint.setColor(miniChartInfo.colors.max.color);
        canvas.drawCircle(x, y, dotRadius, paint);
      } else if(miniChartInfo.colors.min.valid && value == (float) miniChartData.qMin) {
        paint.setColor(miniChartInfo.colors.min.color);
        canvas.drawCircle(x, y, dotRadius, paint);
      } else if(miniChartInfo.colors.first.valid && i == 0) {
        paint.setColor(miniChartInfo.colors.first.color);
        canvas.drawCircle(x, y, dotRadius, paint);
      } else if(miniChartInfo.colors.last.valid && i == miniChartData.matrix.rows.size() - 1) {
        paint.setColor(miniChartInfo.colors.last.color);
        canvas.drawCircle(x, y, dotRadius, paint);
      }
      x += padding * 2.0f + bandwidth;
    }
  }
}
