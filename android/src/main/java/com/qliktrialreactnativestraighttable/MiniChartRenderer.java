package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MiniChartRenderer {
  qMiniChart miniChartData = null;
  MiniChartInfo miniChartInfo = null;
  Representation representation = null;
  Rect bounds = new Rect();
  Paint paint = new Paint();
  float horizontalPadding = 20.0f;
  float verticalPadding = 8.0f;
  float bandwidth = 0.0f;
  float padding = 0.0f;
  float scale = 0.0f;
  float yScale = 1.0f;
  float xAxis = 0.0f;

  public MiniChartRenderer(qMiniChart chartData, Representation representation ) {
    this.representation = representation;
    miniChartData = chartData;
    miniChartInfo = representation.miniChart;
    paint.setAntiAlias(true);
    paint.setFilterBitmap(true);
  }

  public void updateData(qMiniChart chartData, Representation representation) {
    miniChartData = chartData;
    this.representation = representation;
  }

  public void resetScales(Rect bounds) {
    this.bounds = bounds;
    if(miniChartData != null) {
      if(miniChartInfo.yAxis != null && miniChartInfo.yAxis.scale.equals("global")) {
        float min = Math.min((float) representation.globalMin, 0.0f);
        yScale = (float) representation.globalMax - min;
      } else {
        float min = Math.min((float) miniChartData.qMin, 0.0f);
        yScale = (float) (miniChartData.qMax - min);
      }
      setBandwidth();
      setScales();
    }
  }

  protected void setBandwidth() {
    float width = bounds.width() - horizontalPadding;
    float totalBandWidth = width / miniChartData.matrix.rows.size();
    bandwidth = totalBandWidth * 0.8f;
    padding = totalBandWidth * 0.1f;
  }

  protected void setScales() {
    float height = bounds.height() - (verticalPadding * 2.0f);
    scale = height / yScale;
    xAxis = miniChartData.qMin < 0.0 ?  (float)bounds.height() + ((float)miniChartData.qMin * scale) : bounds.height();
  }

  protected void setColor(int index, float value, int count) {
    if(miniChartInfo.colors.max.valid && value == (float) miniChartData.qMax) {
      paint.setColor(miniChartInfo.colors.max.color);
    } else if(miniChartInfo.colors.min.valid && value == (float) miniChartData.qMin) {
      paint.setColor(miniChartInfo.colors.min.color);
    } else if(miniChartInfo.colors.first.valid && index == 0) {
      paint.setColor(miniChartInfo.colors.first.color);
    } else if(miniChartInfo.colors.last.valid && index == count - 1) {
      paint.setColor(miniChartInfo.colors.last.color);
    } else {
      paint.setColor(miniChartInfo.colors.main.color);
    }
  }

  public void render(Canvas canvas) {}
}
