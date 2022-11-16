package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

public class ScreenGuideView extends View {
  Paint paint = new Paint();
  ScreenGuideView(Context context) {
    super(context);
    paint.setStyle(Paint.Style.STROKE);
    paint.setColor(Color.rgb(176, 0, 32));
    paint.setStrokeWidth(PixelUtils.dpToPx(2));
    paint.setPathEffect(new DashPathEffect(new float[] {20f,10f}, 0f));
    setVisibility(INVISIBLE);
  }

  public void fade(int from, int to) {
    Animation animation = new AlphaAnimation(from, to);
    animation.setInterpolator(new DecelerateInterpolator());
    animation.setDuration(800);
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        setVisibility(to == 1 ? VISIBLE : INVISIBLE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    startAnimation(animation);
  }

  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    Rect rect = canvas.getClipBounds();
    int x = rect.left + rect.width();
    int y = rect.height();
//    canvas.drawLine(x, 0, x, y, paint);
  }
}
