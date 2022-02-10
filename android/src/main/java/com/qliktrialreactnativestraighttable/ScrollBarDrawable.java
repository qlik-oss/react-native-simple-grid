package com.qliktrialreactnativestraighttable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScrollBarDrawable extends Drawable {
  Paint paint = new Paint();
  Path path = new Path();
  final float[] corners = new float[]{
    80, 80,
    80, 80,
    80, 80,
    80, 80
  };

  ScrollBarDrawable() {
    paint.setColor(Color.LTGRAY);
  }
  @Override
  public void draw(@NonNull Canvas canvas) {
    RectF r = new RectF(this.getBounds());
    path.reset();
    path.addRoundRect(r,
      corners ,
      Path.Direction.CW);
    canvas.drawPath(path, paint);
  }

  @Override
  public void setAlpha(int i) {
    paint.setAlpha(i);
  }

  @Override
  public void setColorFilter(@Nullable ColorFilter colorFilter) {
    paint.setColorFilter(colorFilter);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSPARENT;
  }
}
