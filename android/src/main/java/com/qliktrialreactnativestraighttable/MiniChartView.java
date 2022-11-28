package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class MiniChartView extends View implements Content {
  Rect bounds = new Rect();
  Paint paint = new Paint();
  DataCell dataCell = null;
  DataColumn dataColumn = null;
  MiniChartRenderer miniChartRenderer = null;

  public MiniChartView(Context context) {
    super(context);
    paint.setColor(Color.BLUE);
  }

  public void setData(DataCell cell, DataColumn column) {
    this.dataCell = cell;
    this.dataColumn = column;
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
  public void updateBackgroundColor(boolean shouldAnimate) {
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
  public void setCellData(DataCell cell, DataRow row, DataColumn column)  {
    dataCell = cell;
  }

  @Override
  public DataCell getCell() {
    return dataCell;
  }

  public void copyToClipBoard() {
    if(miniChartRenderer != null) {
      Bitmap bitmap = renderToBitmap();
      ImageShare imageShare = new ImageShare();
      imageShare.share(bitmap, getContext());
    }
  }

  private Bitmap renderToBitmap() {
    int height = (int)PixelUtils.dpToPx(getHeight());
    int width = (int)PixelUtils.dpToPx(getWidth());
    Rect newBounds = new Rect(0, 0, width, height);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    canvas.drawColor(Color.WHITE);
    miniChartRenderer.resetScales(newBounds);
    miniChartRenderer.render(canvas);
    return bitmap;
  }

  public String getCopyMenuString() {
    return "share";
  }
}
