package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GrabberView extends LinearLayout {
  GrabberButton grabberButton;
  Paint linePaint = new Paint();
  CustomHorizontalScrollView scrollView;

  class TouchListener implements OnTouchListener {
    float dX = 0;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      int action = motionEvent.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN: {
          GrabberView.this.scrollView.setDisableIntercept(true);
          dX = GrabberView.this.getX() - motionEvent.getRawX();
          GrabberView.this.resetLinePaint(Color.BLACK);
          return true;
        }
        case MotionEvent.ACTION_MOVE: {
          float x = motionEvent.getRawX() + dX;
          GrabberView.this.setTranslationX(x);
          return true;
        }
        case MotionEvent.ACTION_UP: {
          GrabberView.this.resetLinePaint(TableTheme.borderBackgroundColor);
          GrabberView.this.scrollView.setDisableIntercept(false);
          return  true;
        }
      }
      return false;
    }
  }
  public class GrabberButton extends View {
    public GrabberButton(View parent) {
      super(parent.getContext());
    }
  }

  public GrabberView(Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.scrollView = scrollView;
    grabberButton = new GrabberButton(this);
    grabberButton.setBackgroundColor(Color.BLACK);
    grabberButton.setAlpha(0.1f);
    grabberButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight));
    this.addView(grabberButton);
    grabberButton.setOnTouchListener(new TouchListener());
    linePaint.setColor(TableTheme.borderBackgroundColor);
    linePaint.setStrokeWidth(PixelUtils.dpToPx(1));
  }

  public void resetLinePaint(int color) {
    linePaint = new Paint();
    linePaint.setColor(color);
    linePaint.setStrokeWidth(PixelUtils.dpToPx(1));
    this.invalidate();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int width = canvas.getWidth() / 2;
    canvas.drawLine(width, 0, width, canvas.getHeight(), linePaint);
  }
}
