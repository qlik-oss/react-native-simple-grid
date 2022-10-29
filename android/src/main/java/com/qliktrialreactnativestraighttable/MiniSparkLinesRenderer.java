package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class MiniSparkLinesRenderer extends MiniChartRenderer {
  Path path = new Path();
  MiniDotsChartRenderer dots = null;
  public MiniSparkLinesRenderer(qMiniChart chartData, Representation representation) {
    super(chartData, representation);
    dots = new MiniDotsChartRenderer(chartData, representation);
  }

  @Override
  public void render(Canvas canvas) {
    float x = padding + (horizontalPadding / 2.0f);
    paint.setColor(miniChartInfo.colors.main.color);
    startPath(x, canvas);

    for(int i = 1; i < miniChartData.matrix.rows.size(); i++) {
      float value = (float) miniChartData.matrix.rows.get(i).columns.get(1).qNum;
      float height = value * scale;
      float y = xAxis - height  ;
      float x2 = x + padding * 2.0f + bandwidth;
      path.lineTo(x2, y);
      x += padding * 2.0f + bandwidth;
    }
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(2);
    canvas.drawPath(path, paint);
    if(miniChartInfo.showDots) {
      dots.render(canvas);
    } else {
      dots.renderColorValues(canvas);
    }
  }
  protected void startPath(float x, Canvas canvas) {
    path.reset();

    if(miniChartData.matrix.rows.size() > 0) {
      float value = (float) miniChartData.matrix.rows.get(0).columns.get(1).qNum;
      float height = value * scale;
      float y = xAxis - height  ;
      path.moveTo(x, y);
    }
  }

  @Override
  public void updateData(qMiniChart chartData, Representation representation) {
    super.updateData(chartData, representation);
    if(dots != null) {
      dots.updateData(chartData, representation);
    }
  }

  public void resetScales(Rect bounds) {
    super.resetScales(bounds);
    if(dots != null) {
      dots.resetScales(bounds);
    }
  }
}
