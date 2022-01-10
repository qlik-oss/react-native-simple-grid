package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.react.common.MapBuilder;

import java.util.List;

public class GrabberView extends LinearLayout {
  GrabberButton grabberButton;
  Paint linePaint = new Paint();
  CustomHorizontalScrollView scrollView;
  DataProvider dataProvider = null;
  HeaderView headerView = null;
  List<GrabberView> grabbers = null;
  private final int column;

  class TouchListener implements OnTouchListener {
    float dX = 0;
    float lastX = 0;
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
      int action = motionEvent.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN: {
          GrabberView.this.scrollView.setDisableIntercept(true);
          lastX = motionEvent.getRawX();
          dX = GrabberView.this.getX() - motionEvent.getRawX();
          GrabberView.this.resetLinePaint(Color.BLACK);
          return true;
        }
        case MotionEvent.ACTION_MOVE: {
          float x = motionEvent.getRawX() + dX;
          float motionDx = motionEvent.getRawX() - lastX;
          GrabberView.this.setTranslationX(x);

          motionDx = Math.round(motionDx);
          for(int i = column + 1; i < grabbers.size(); i++ ) {
            GrabberView grabberView = grabbers.get(i);
            grabberView.updateTranslation(motionDx);
          }
          dataProvider.updateWidth(motionDx, GrabberView.this.column);
          GrabberView.this.updateHeader(motionDx);
          lastX = motionEvent.getRawX();
          return true;
        }
        case MotionEvent.ACTION_UP: {
          GrabberView.this.resetLinePaint(TableTheme.borderBackgroundColor);
          GrabberView.this.scrollView.setDisableIntercept(false);
          GrabberView.this.scrollView.updateLayout();
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

  public GrabberView(int column, Context context, CustomHorizontalScrollView scrollView) {
    super(context);
    this.column = column;
    this.scrollView = scrollView;
    grabberButton = new GrabberButton(this);
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

  public void setDataProvider(DataProvider provider) {
    this.dataProvider = provider;
  }

  public void setHeaderView(HeaderView headerView) {
    this.headerView = headerView;
  }

  public void setGrabbers(List<GrabberView> grabbers) {
    this.grabbers = grabbers;
  }

  public void updateHeader(float dxMotion) {
    View view = headerView.getChildAt(column);
    LinearLayout.LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
    layoutParams.width += dxMotion;
    view.setLayoutParams(layoutParams);
  }

  public void updateTranslation(float dx) {
    float x = this.getX() + dx;
    this.setTranslationX(x);
  }
}
