package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewFactory {
  List<DataColumn> dataColumns = new ArrayList<>();
  HeaderView headerView ;

  public HeaderView getHeaderView() {
    return headerView;
  }

  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public HeaderViewFactory(ReadableArray readableArray, Context context) {
    headerView = new HeaderView(context);
    headerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, TableTheme.headerHeight));
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setElevation((int)PixelUtils.dpToPx(4));
    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for(int i = 0; i < readableArray.size(); i++) {
      DataColumn column = new DataColumn(readableArray.getMap(i));
      dataColumns.add(column);
      TextView text = new TextView(context);
      int padding = (int) PixelUtils.dpToPx(16);
      text.setMaxLines(1);
      text.setTypeface(text.getTypeface(), Typeface.BOLD);
      text.setEllipsize(TextUtils.TruncateAt.END);
      text.setText(column.label);
      text.setPadding(padding, padding, padding, padding);
      text.setLayoutParams(new LinearLayout.LayoutParams(column.width, TableTheme.headerHeight));
      headerView.addView(text);
    }
  }
}
