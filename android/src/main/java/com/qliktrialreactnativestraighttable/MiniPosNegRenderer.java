package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;

public class MiniPosNegRenderer extends MiniChartRenderer{
  public MiniPosNegRenderer(qMiniChart chartData, Representation representation) {
    super(chartData, representation);
  }

  @Override
  public void render(Canvas canvas) {
    float x = padding + (horizontalPadding / 2.0f);
    float barHeight = (bounds.height() - (verticalPadding*2)) / 2.0f;
    xAxis = bounds.height() / 2.0f;
    for(int i = 0; i < miniChartData.matrix.rows.size(); i++) {
      float value = (float) miniChartData.matrix.rows.get(i).columns.get(1).qNum;
      float y = xAxis - barHeight ;
      float b = xAxis;
      if(value < 0) {
        y += barHeight;
        b = bounds.height() - verticalPadding;
      }
      setColor(value);
      canvas.drawRect(x, y, x + bandwidth, b, paint);
      x += padding * 2.0f + bandwidth;
    }
  }

  protected void setColor(float value) {
    if(value < 0) {
      paint.setColor(miniChartInfo.colors.negative.color);
    } else {
      paint.setColor(miniChartInfo.colors.positive.color);
    }
  }
}
