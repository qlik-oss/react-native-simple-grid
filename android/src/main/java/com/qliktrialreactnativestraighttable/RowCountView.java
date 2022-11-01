package com.qliktrialreactnativestraighttable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class RowCountView extends RelativeLayout {
  final int margin = (int)PixelUtils.dpToPx(8);
  RelativeLayout container;
  TextView textView;

  public RowCountView(Context context, TableView tableView) {
    super(context);

    container = new RelativeLayout(context);
    container.setGravity(Gravity.RIGHT);

    textView = new TextView(context);
    RelativeLayout.LayoutParams textLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
    textLayout.rightMargin = margin;
    textLayout.topMargin = margin;
    textView.setLayoutParams(textLayout);

    int h = tableView.getMeasuredHeight();

    container.setZ(4);
    container.setY(h - TableTheme.headerHeight);

    FrameLayout.LayoutParams frameLayout = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, TableTheme.headerHeight);
    container.setLayoutParams(frameLayout);
    container.setBackgroundColor(Color.WHITE);

    container.addView(textView);
    addView(container);
  }

  public void update(int windowMin, int windowMax, int total) {
    String text = windowMin + " - " + windowMax + " of " + total; // TODO: Translate
    textView.setText(text);
    textView.postInvalidate();
  }


}
