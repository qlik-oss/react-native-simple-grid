package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class MiniDotsChartRenderer extends MiniChartRenderer{
  public MiniDotsChartRenderer(qMiniChart chartData, Representation representation) {
    super(chartData, representation);
  }

  @Override
  public void render(Canvas canvas) {
    float x = padding + (horizontalPadding / 2.0f);
    paint.setColor(miniChartInfo.colors.main.color);
    for(int i = 0; i < miniChartData.matrix.rows.size(); i++) {
      float value = (float) miniChartData.matrix.rows.get(i).columns.get(1).qNum;
      float height = value * scale;
      float y = xAxis - height  ;
      setColor(i, value, miniChartData.matrix.rows.size());
      canvas.drawCircle(x, y, 6, paint);
      x += padding * 2.0f + bandwidth;
    }
  }
}
