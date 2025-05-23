package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DragBox extends View {
  final DragBoxEventHandler dragBoxEventHandler;
  final TableView tableView;
  final boolean isFirstColumnBox;
  boolean shown = false;
  boolean pressed = false;
  int height;
  int columnId;
  Rect bounds;
  Rect grabberLine;
  Rect grabberLineTwo;
  Rect drawRect;
  Rect drawBottomFill;
  Paint paint = new Paint();


  public DragBox(Context context, TableView tableView, DragBoxEventHandler dragBoxEventHandler, boolean isFirstColumnBox){
    super(context);
    this.tableView = tableView;
    this.setOnTouchListener(new DragBox.TouchListener());
    this.dragBoxEventHandler = dragBoxEventHandler;
    this.isFirstColumnBox = isFirstColumnBox;
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(1, 1);
    setX(-1);
    setY(-1);
    setZ(PixelUtils.dpToPx(1));
    setLayoutParams(layoutParams);
  }

  public void show(Rect bounds, int column) {
    this.columnId = column;
    this.bounds = bounds;
    if(this.bounds == null) {
      return;
    }
    int width =  bounds.right - bounds.left;
    height = bounds.bottom - bounds.top;
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
    setX(bounds.left);
    setY(bounds.top);
    setAlpha(1);
    setLayoutParams(layoutParams);
    int inset = width / 4;
    drawRect = new Rect(0, 0, layoutParams.width, layoutParams.height);
    drawBottomFill = new Rect(0, layoutParams.height - 35, layoutParams.width, layoutParams.height);
    grabberLine = new Rect(inset, layoutParams.height - 10, layoutParams.width - inset, layoutParams.height - 10);
    grabberLineTwo = new Rect(inset, layoutParams.height - 15, layoutParams.width - inset, layoutParams.height - 15);
    shown = true;
  }

  public void setScrollListener(CustomRecyclerView recyclerView) {
    recyclerView.addOnScrollListener(new OnScrollListener());
  }

  public void hide() {
    this.bounds = null;
    shown = false;
    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(1, 1);
    setX(-1);
    setY(-1);
    setAlpha(0);
    setLayoutParams(layoutParams);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if(!shown && drawRect == null) {
      return;
    }
    super.onDraw(canvas);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.DKGRAY);
    canvas.drawRect(drawBottomFill, paint);

    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(10);
    canvas.drawRect(drawRect, paint);

    paint.setStrokeWidth(2);
    paint.setColor(Color.WHITE);
    canvas.drawRect(grabberLine, paint);
    canvas.drawRect(grabberLineTwo, paint);
  }

  class TouchListener implements OnTouchListener {
    float dY = 0;


    @Override
    public boolean onTouch(View view, MotionEvent event) {
      if(bounds == null) {
        return true;
      }

      int action = event.getAction();
      switch (action) {
        case MotionEvent.ACTION_DOWN: {
          tableView.scrollView.setDisableIntercept(true);
          pressed = true;
          dY = getY() - event.getRawY();
          return true;
        }
        case MotionEvent.ACTION_MOVE: {
          float y = event.getRawY() + dY;
          if(y < tableView.getContentTop()) {
            return true;
          }
          if(y + height > tableView.getContentBottom()) {
            return true;
          }
          bounds.top = (int) y;
          bounds.bottom = (int) y + height;
          setTranslationY(y);
          dragBoxEventHandler.fireEvent("dragged", columnId, isFirstColumnBox);
          return true;
        }
        case MotionEvent.ACTION_UP: {
          pressed = false;
          hide();
          tableView.scrollView.setDisableIntercept(false);
          return true;
        }
      }
      return false;
    }
  }

  class OnScrollListener extends RecyclerView.OnScrollListener {
    @Override
    public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
      if(bounds == null) {
        return;
      }
      int y = (int) getY() - dy;
      bounds.top = y;
      bounds.bottom = y + height;
      setTranslationY(y);
    }
  }
}
