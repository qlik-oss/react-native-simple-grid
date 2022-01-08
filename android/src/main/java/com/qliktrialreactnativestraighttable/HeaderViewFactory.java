package com.qliktrialreactnativestraighttable;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.react.bridge.ReadableArray;

import java.util.ArrayList;
import java.util.List;

public class HeaderViewFactory {
  public List<DataColumn> getDataColumns() {
    return dataColumns;
  }

  List<DataColumn> dataColumns = new ArrayList<>();

  public LinearLayout getHeaderView() {
    return headerView;
  }

  LinearLayout headerView ;
  public HeaderViewFactory(ReadableArray readableArray, Context context) {
    headerView = new LinearLayout(context);
    headerView.setOrientation(LinearLayout.HORIZONTAL);
    headerView.setMinimumWidth(1000);

    headerView.setBackgroundColor(TableTheme.headerBackgroundColor);
    for(int i = 0; i < readableArray.size(); i++) {
      DataColumn column = new DataColumn(readableArray.getMap(i));
      dataColumns.add(column);
      TextView text = new TextView(context);
      text.setText(column.label);
      text.setPadding(16, 16, 16, 16);

      text.setLayoutParams(new LinearLayout.LayoutParams(
        (int)PixelUtils.dpToPx((float) (column.width - 50)), TableTheme.rowHeight)
      );
      headerView.addView(text);
    }

  }
}
